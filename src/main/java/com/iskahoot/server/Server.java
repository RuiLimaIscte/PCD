package com.iskahoot.server;

import com.iskahoot.common.messages.AnswerFromClient;
import com.iskahoot.common.models.Quiz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;

//TODO Guardar referencia a todos os clients ligados
public class Server {

    public static final int PORTO = 8080;
    private int porto;
    private ServerSocket serverSocket;
    private final List<DealWithClient> clients = new CopyOnWriteArrayList<DealWithClient>();

    public GameState getGameState() {
        return gameState;
    }

    //TODO TESTE
//    private CountDownLatch latch;
    GameState gameState = new GameState(new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
            loadFromFile("src/main/resources/questions.json").getQuestions()));

    public static void main ( String [] args ) {
        try {
            new Server().startServing ();
        } catch ( IOException e ) {
// ...
        }
    }
// ...
    public Server() throws IOException {
        this.porto=PORTO;
    }

    public Server(int porto) throws IOException {
        this.porto=porto;
    }

    public void startServing () throws IOException {
        try {
            serverSocket = new ServerSocket ( porto );
            while ( true ) {
                //wait for a connection
                Socket socket = serverSocket.accept();
//                DealWithClient handler = new DealWithClient(socket);
                DealWithClient handler = new DealWithClient(socket,this);
                clients.add(handler);
                handler.start();

                // ADICIONAR ISTO PARA TESTAR O TEMPO:
//                if (clients.size() == 1) { // Apenas para teste, arranca com 1 jogador
//                    new GameSession(clients).start();
//                }
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
