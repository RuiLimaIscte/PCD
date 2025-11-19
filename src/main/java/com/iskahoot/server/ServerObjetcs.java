package com.iskahoot.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerObjetcs {

    public static final int PORTO = 8080;
    private ServerSocket serverSocket;
//    private BufferedReader in ;
//    private PrintWriter out ;

    public static void main ( String [] args ) {
        try {
            new ServerObjetcs (). startServing ();
        } catch ( IOException e ) {
// ...
        }
    }
// ...

    public void startServing () throws IOException {
        try {
            serverSocket = new ServerSocket ( PORTO );
            while ( true ) {
                WaitConnections();
            }
        } catch ( IOException e ) {
            e . printStackTrace ();
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
    void WaitConnections () throws IOException {
        Socket connection = serverSocket.accept(); // Wait for connection at port
        DealWithClientObjects handler = new DealWithClientObjects(connection);
        handler.start();
    }

}
