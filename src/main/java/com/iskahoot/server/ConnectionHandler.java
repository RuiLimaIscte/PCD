package com.iskahoot.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionHandler extends Thread {
    private Socket connection;
    private BufferedReader in; //TODO mudar para ObjectInputStream
    private PrintWriter out;//TODO mudar para ObjectOutputStream

    public ConnectionHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            setStreams();
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void setStreams() throws IOException {
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true); //TODO mudar para ObjectOutputStream
        in = new BufferedReader( new InputStreamReader (connection.getInputStream())); //TODO mudar para ObjectInputStream
    }

    private void serve() throws IOException {
        while (true) {
            String str = in.readLine();
            if (str.equals("FIM"))
                break;
            System.out.println("Eco:" + str);
            out.println(str);
        }
    }

    public void closeConnection() {
        try {
            if(connection != null)
                connection.close();
            if(in != null)
                in.close();
            if(out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

