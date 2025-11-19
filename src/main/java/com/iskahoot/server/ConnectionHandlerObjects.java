package com.iskahoot.server;

import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;

import java.io.*;
import java.net.Socket;

public class ConnectionHandlerObjects extends Thread {
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ConnectionHandlerObjects(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            setStreams();
            serve();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void setStreams() throws IOException {
        out = new ObjectOutputStream(connection.getOutputStream());
        out.flush(); // necessário para inicializar corretamente o ObjectInputStream no cliente
        in = new ObjectInputStream(connection.getInputStream());
    }

    private void serve() throws IOException, ClassNotFoundException {
        while (true) {
            Object obj = in.readObject();
            if (obj instanceof ReceptionConfirmationMessage) {
                ReceptionConfirmationMessage confirmation = (ReceptionConfirmationMessage) obj;
                System.out.println("Confirmação recebida do cliente: " + confirmation.getReceivedAtMillis());
            }
        }
    }

    public void sendTimeMessage(long currentTimeMillis) throws IOException {
        TimeMessage timeMessage = new TimeMessage(currentTimeMillis);
        out.writeObject(timeMessage);
        out.flush();
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
