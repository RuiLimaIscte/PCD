package com.iskahoot.server;

import com.iskahoot.common.messages.AnswerFromClient;
import com.iskahoot.common.models.Quiz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;

//TODO Guardar referencia a todos os clients ligados
public class Server {

    public static final int PORTO = 8080;
    private int porto;
    private ServerSocket serverSocket;
    private final List<DealWithClient> clients = new CopyOnWriteArrayList<DealWithClient>();
    //TODO TESTE
//    private CountDownLatch latch;
//    private GameState gameState = new GameState(new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
//            loadFromFile("src/main/resources/questions.json").getQuestions()));
    private RoomManager roomManager = RoomManager.getInstance();

    public static void main ( String [] args ) {
        try {
            new Server().startServing(args);
        } catch ( IOException e ) {

        }
    }

    public Server() throws IOException {
        this.porto=PORTO;
    }

    public Server(int porto) throws IOException {
        this.porto=porto;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

//    public GameState getGameState() {
//        return gameState;
//    }

    public void startServing (String[] args) throws IOException {
        configurarJogoInicial(args);

        try {
            serverSocket = new ServerSocket ( porto );
            while ( true ) {
                //wait for a connection
                Socket socket = serverSocket.accept();
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

    private void configurarJogoInicial(String[] args) {
        int numEquipas = 0;
        int jogadoresPorEquipa = 0;

            Scanner sc = new Scanner(System.in);
            System.out.println("Configuração do Jogo");

            // Validação simples para equipas
            do {
                System.out.print("Número de Equipas: ");
                while (!sc.hasNextInt()) {
                    System.out.println("Por favor insira um número inteiro.");
                    sc.next();
                }
                numEquipas = sc.nextInt();
            } while (numEquipas <= 0);

            // Validação simples para jogadores
            do {
                System.out.print("Jogadores por Equipa: ");
                while (!sc.hasNextInt()) {
                    System.out.println("Por favor insira um número inteiro.");
                    sc.next();
                }
                jogadoresPorEquipa = sc.nextInt();
            } while (jogadoresPorEquipa <= 0);

        String codigo = getRoomManager().createRoom(numEquipas, jogadoresPorEquipa);

        //TODO apaenas para teste, depois é precioso alterar para aguradar todos os jogadores
        // Forçar o início do jogo para o gameState ser criado e não dar erro no cliente
        // Nota: Isto é apenas para teste, num jogo real esperarias pelos jogadores todos.
        Room sala = getRoomManager().getRoom(codigo);
        sala.startGame();

        System.out.println(" \n Código da Sala: " + codigo);
        System.out.println(" Configuração: " + numEquipas + " equipas de " + jogadoresPorEquipa + " jogadores.");
    }
}


