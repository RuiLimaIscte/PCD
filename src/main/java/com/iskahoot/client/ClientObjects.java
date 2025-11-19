package com.iskahoot.client;

import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;
import com.iskahoot.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientObjects {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket ;

    public static void main ( String [] args ) {
        new ClientObjects (). runClient ();
    }
    public void runClient () {
        try {
            connectToServer();
            setStreams();
//            sendMessages();
        } catch (IOException e) {// ERRO ...
            e . printStackTrace ();
        } finally {// a fechar ...
            closeCon();
        }
    }

    private void closeCon() {
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

    void connectToServer () throws IOException {
        InetAddress endereco = InetAddress.getByName ( null );
        System.out.println ( " Ligacao a : " + endereco + " : " + Server.PORTO );
        socket = new Socket (endereco , Server.PORTO );
        System.out.println("socket: " + socket);
    }

//    void sendMessages () throws IOException {
//        for (int i = 0; i < 10; i ++) {
//            out.println ( " Ola " + i );
//            String str = in.readLine();
//            System.out.println ( str );
//            try {
//                Thread.sleep ( 3000 );
//            } catch ( InterruptedException e ) {
//                e . printStackTrace ();
//            }
//        }
//        out.println ( " FIM " );
//    }

    private void setStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }


}
