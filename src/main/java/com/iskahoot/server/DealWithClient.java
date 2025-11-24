package com.iskahoot.server;

import com.iskahoot.common.messages.Mensagem;
import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;

import java.io.*;
import java.net.Socket;

public class DealWithClient extends Thread {
    private Socket connection;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public DealWithClient(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            setStreams();
            sendTime();
            serve();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (connection != null)
                connection.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setStreams() throws IOException {
        out = new ObjectOutputStream(connection.getOutputStream());
        out.flush();
        in = new ObjectInputStream(connection.getInputStream());
    }

    private void serve() throws IOException {
        while (true) {
            Object obj;
            try {
                obj = in.readObject();
                if (((Mensagem) obj).getMessage().equals("FIM"))
                    break;
                System.out.println("Eco: " + ((Mensagem) obj).getMessage() + " id: " + ((Mensagem) obj).getId());
                out.writeObject(obj);
            } catch (IOException | ClassNotFoundException e) {

            }
        }
    }

    void sendTime() throws IOException, ClassNotFoundException {
        TimeMessage timeMessage = new TimeMessage(System.currentTimeMillis());
        out.writeObject(timeMessage);
        ReceptionConfirmationMessage confirmation = (ReceptionConfirmationMessage) in.readObject();
        System.out.println("Confirmação recebida do cliente: " + confirmation.getReceivedAtMillis());


//        for (int i = 0; i < 10; i ++) {
//            Mensagem message= new Mensagem(i,"ola");
//            out.writeObject(message);
//            Mensagem str = (Mensagem)in.readObject();
//            System.out.println ( str );
//            try {
//                Thread.sleep ( 3000 );
//            } catch ( InterruptedException e ) {
//                e . printStackTrace ();
//            }
//        }
//        out.writeObject(new Mensagem(-1,"FIM"));
    }


}

