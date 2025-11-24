package com.iskahoot.client;

import com.iskahoot.common.messages.Mensagem;
import com.iskahoot.common.messages.ReceptionConfirmationMessage;
import com.iskahoot.common.messages.TimeMessage;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.server.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;

public class ClientObjects {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket ;
    private long displayedTime;

    public static void main ( String [] args ) {
        new ClientObjects (). runClient ();
    }
    public void runClient () {
        try {
            connectToServer();
            setStreams();
            sendConfirmation();
        } catch (IOException | ClassNotFoundException e) {// ERRO ...
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

    void sendConfirmation () throws IOException, ClassNotFoundException {
        TimeMessage timeMessage = (TimeMessage) in.readObject();
        ReceptionConfirmationMessage receptionConfirmationMessage = new ReceptionConfirmationMessage(System.currentTimeMillis());
        out.writeObject(receptionConfirmationMessage);
        System.out.println("Confirmação enviada!");
//        long serverSentAt = timeMessage.getCurrentTimeMillis();
//        long clientReceivedAt = receptionConfirmationMessage.getReceivedAtMillis();
//        long latency = clientReceivedAt - serverSentAt;
//        displayedTime = serverSentAt - latency;
        Quiz quiz = new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
                loadFromFile("src/main/resources/questions.json").getQuestions());

        new SimpleClientGUI(quiz.getQuestions(), "Client 1");//TODO a thread nao vai funcionar bem assim, por
        SimpleClientGUI.updateTimerLabel(30); // TODO a thread em while true talvez

        try {
                Thread.sleep ( 3000 );
            } catch ( InterruptedException e ) {
                e . printStackTrace ();
            }
//
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

    private void setStreams() throws IOException {
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

    }
    public long getdisplayedTime() {
        return displayedTime;
    }

}
