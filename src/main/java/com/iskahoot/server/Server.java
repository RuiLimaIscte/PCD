//package com.iskahoot.server;
//
//import com.iskahoot.common.messages.ConnectionMessage;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//
//public class Server {
//
//    public static final int PORTO = 8080;
//    private int port;
//    private ServerSocket serverSocket;
//
//    private final Map<GameState, List<DealWithClient2>> clientsByGame = new HashMap<>();
//
//    private int roomCounter = 0;
//
//    public static void main ( String [] args ) {
//        try {
//            new Server().startServing();
//        } catch ( IOException e ) {
//
//        }
//    }
//
//    public Server() throws IOException {
//        this.port =PORTO;
//    }
//
//    public void startServing () throws IOException {
//
//        // While true Wait for "new numberteams numberplayers" if(someone writes that then creates new thread for game)
////        new thread para haverem jogos ao mesmo tempo -> configurarJogoInicial( numberteams, numberplayers);
//
//    }
//
//    private void configurarJogoInicial(int numberTeams, int numberPlayers) {
//        System.out.println(" \n Código da Sala: game" + roomCounter);
//        System.out.println(" Configuração: " + numberTeams + " equipas de " + numberPlayers + " jogadores.");
//
//        GameState gameState = new GameState(new Game("game"+roomCounter, numberTeams, numberPlayers));
//        clientsByGame.put(gameState, new ArrayList<>());
//        roomCounter++;
//
//        gameState.getGame().startGame();
//
//        // wait for players to join the room
//        try {
//            serverSocket = new ServerSocket (port);
//            while ( true ) {
//                //wait for a connection
//                Socket socket = serverSocket.accept();
//                DealWithClient2 client = new DealWithClient2(socket, gameState);
//                client.start();
//                ConnectionMessage connectionMessage;
//                while (true) {
//                    Object obj;
//                    try {
//                        obj = new ObjectInputStream(socket.getInputStream()).readObject();
//                        if (obj instanceof ConnectionMessage) {
//                            connectionMessage = (ConnectionMessage) obj;
//                            System.out.println("Cliente conectado: " + connectionMessage.getClientCode());
//                            break;
//                        }
//                    } catch (ClassNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                //
//                if(connectionMessage.getGameCode().equals(gameState.getGame().getGameCode())){
//                    if(gameState.getGame().addPlayer(connectionMessage.getTeamCode(), connectionMessage.getClientCode())) {
//                        clientsByGame.get(gameState).add(client);
//                        System.out.println("Novo cliente conectado ao jogo " + gameState.getGame().getGameCode());
//                    } else {
//                        System.out.println("Sala cheia. Cliente não pode se conectar ao jogo " + gameState.getGame().getGameCode());
//                        socket.close();
//                    }
//                }
//
//
//            }
//        } catch ( IOException e ) {
//            e.printStackTrace ();
//        } finally {
//            if(serverSocket != null) {
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//
//}
//
//
