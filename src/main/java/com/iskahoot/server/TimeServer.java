package com.iskahoot.server;

import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class TimeServer {

    private final int port;
    private final int intervalSeconds; // Intervalo de envio
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;

    public TimeServer(int port, int intervalSeconds) {
        this.port = port;
        this.intervalSeconds = intervalSeconds;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor de tempo iniciado na porta " + port);

        // Thread que aceita clientes
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    clients.add(handler);
                    handler.start();
                    System.out.println("Cliente conectado: " + socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Scheduler que envia TimeMessage periodicamente
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::sendTimeToAllClients, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    private void sendTimeToAllClients() {
        long currentTime = System.currentTimeMillis();
        Iterator<ClientHandler> iterator = clients.iterator();

        while (iterator.hasNext()) {
            ClientHandler client = iterator.next();
            try {
                client.sendTime(currentTime);

                // Espera confirmação em até 2s
                boolean confirmed = client.waitForConfirmation(2000);
                if (!confirmed) {
                    System.out.println("Cliente não confirmou, removendo: " + client.getSocket());
                    client.close();
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
                client.close();
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new TimeServer(2424, 5).start(); // envia a cada 60 segundos
    }

    // --------------------------
    // Cliente conectado
    // --------------------------
    private static class ClientHandler extends Thread {
        private final Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private boolean confirmed;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                // Thread dedicada a receber confirmações
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof ReceptionConfirmationMessage) {
                        confirmed = true;
                        System.out.println("Confirmação recebida do cliente: " + socket);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                close();
            }
        }

        public void sendTime(long currentTimeMillis) throws IOException {
            if (socket.isClosed()) return;
            confirmed = false;
            out.writeObject(new TimeMessage(currentTimeMillis));
            out.flush();
        }

        public boolean waitForConfirmation(long timeoutMillis) {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < timeoutMillis) {
                if (confirmed) return true;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return false;
        }

        public void close() {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Socket getSocket() {
            return socket;
        }
    }
}
