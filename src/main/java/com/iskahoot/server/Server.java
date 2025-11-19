package com.iskahoot.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
//TODO Guardar referencia a todos os clients ligados
public class Server {

    public static final int PORTO = 8080;
    private ServerSocket serverSocket;

    public static void main ( String [] args ) {
        try {
            new Server().startServing ();
        } catch ( IOException e ) {
// ...
        }
    }
// ...

    public void startServing () throws IOException {
        try {
            serverSocket = new ServerSocket ( PORTO );
            while ( true ) {
                //wait for a connection
                Socket socket = serverSocket.accept();
                DealWithClient handler = new DealWithClient(socket);
                handler.start();
            }
        } catch ( IOException e ) {
            e.printStackTrace ();
        } finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//    void WaitConnections () throws IOException {
//        Socket connection = serverSocket.accept();
//        ConnectionHandler handler = new ConnectionHandler(connection);
//        handler.start();
//    }

}
