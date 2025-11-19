package com.iskahoot.server;

import com.iskahoot.common.messages.Mensagem;

import java.io.*;
import java.net.Socket;

public class DealWithClient extends Thread {
    private Socket connection;
//    private BufferedReader in; //TODO mudar para ObjectInputStream
//    private PrintWriter out;//TODO mudar para ObjectOutputStream
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public DealWithClient(Socket connection) {
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
//        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true); //TODO mudar para ObjectOutputStream
//        in = new BufferedReader( new InputStreamReader (connection.getInputStream())); //TODO mudar para ObjectInputStream
        out = new ObjectOutputStream(connection.getOutputStream());
        out.flush();
        in = new ObjectInputStream(connection.getInputStream());
    }

    private void serve() throws IOException {
        while (true) {
            Mensagem str;
            try {
                str = (Mensagem) in.readObject();
                if (str.getMessage().equals("FIM"))
                    break;
                System.out.println("Eco: " + str.getMessage() + " id: " + str.getId());
                out.writeObject(str);
            } catch (IOException | ClassNotFoundException e) {

            }
        }
    }


}

