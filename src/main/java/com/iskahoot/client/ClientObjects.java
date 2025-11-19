package com.iskahoot.client;

import com.iskahoot.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientObjects {
    private BufferedReader  in ;   //TODO mudar para ObjectInputStream
    private PrintWriter out ;   //TODO mudar para ObjectOutputStream
    private Socket socket ;

    public static void main ( String [] args ) {
        new Client (). runClient ();
    }
    public void runClient () {
        try {
            connectToServer();
            setStreams();
            sendMessages();
        } catch (IOException e) {// ERRO ...
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

    void sendMessages () throws IOException {
        for (int i = 0; i < 10; i ++) {
            out.println ( " Ola " + i );
            String str = in.readLine();
            System.out.println ( str );
            try {
                Thread.sleep ( 3000 );
            } catch ( InterruptedException e ) {
                e . printStackTrace ();
            }
        }
        out.println ( " FIM " );
    }

    private void setStreams() throws IOException {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); //TODO mudar para ObjectOutputStream
        in = new BufferedReader ( new InputStreamReader(socket.getInputStream())); //TODO mudar para ObjectInputStream
    }


}
