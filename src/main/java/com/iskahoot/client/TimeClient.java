package com.iskahoot.client;

import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Cliente que se conecta ao servidor de tempo,
 * recebe TimeMessage e responde com ReceptionConfirmationMessage.
 */
public class TimeClient {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public static void main(String[] args) {
        new TimeClient().runClient();
    }

    public void runClient() {
        try {
            connectToServer();
            setStreams();
            listenForTimeMessages();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException {
        InetAddress endereco = InetAddress.getByName(null); // localhost
        socket = new Socket(endereco, 2424); // porta do servidor
        System.out.println("Cliente conectado ao servidor de tempo: " + socket);
    }


    private void listenForTimeMessages() throws IOException, ClassNotFoundException {
        while (true) {
            Object obj = in.readObject();
            if (obj instanceof TimeMessage) {
                TimeMessage timeMessage = (TimeMessage) obj;
                long serverTime = timeMessage.getCurrentTimeMillis();
                System.out.println("Hora do servidor: " + serverTime);

                // Aqui podes atualizar o relógio local se quiser
                long localTimeBefore = System.currentTimeMillis();
                System.out.println("Hora local antes da sincronização: " + localTimeBefore);

                // Envia confirmação de recepção
                ReceptionConfirmationMessage confirmation =
                        new ReceptionConfirmationMessage(System.currentTimeMillis());
                out.writeObject(confirmation);
                out.flush();
                System.out.println("Confirmação enviada!");
            }
        }
    }

    private void closeConnection() {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStreams() throws IOException {
        // Sempre criar ObjectOutputStream primeiro
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
}
