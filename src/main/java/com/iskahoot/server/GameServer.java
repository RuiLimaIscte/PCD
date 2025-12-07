package com.iskahoot.server;

import com.iskahoot.common.messages.ConnectionMessage;
import com.iskahoot.common.messages.CurrentQuestion;
import com.iskahoot.common.models.Question;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private int port = 8080;
    private int roomCounter = 0;

    // Shared Map: Maps a GameState to a list of connected client threads
    private Map<GameState, List<DealWithClient>> clientsByGame = new HashMap<>();

    public GameServer() throws IOException {
    }

    public static void main(String[] args) {
        try {
            new GameServer().startServing();
        } catch (IOException e) {

        }
    }

    public void startServing() throws IOException {
        System.out.println("Server started on port " + port);

        // 1. Start the Thread to listen for Console Commands (Create Game)
        new Thread(this::handleConsoleCommands).start();

        // 2. Start the Network Listener
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                // Wait for a connection
                Socket socket = serverSocket.accept();

                // 3. Delegate the client to a separate thread immediately
                // We pass the list of games so the client can find the one they want to join
                new DealWithClient(socket).start();
            }
        } finally {
            if (serverSocket != null) serverSocket.close();
        }
    }

    // Logic to read "new 2 5" from keyboard
    private void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                String command = scanner.next();
                if (command.equalsIgnoreCase("new")) {
                    int nTeams = scanner.nextInt();
                    int nPlayers = scanner.nextInt();
                    configurarJogoInicial(nTeams, nPlayers);
                }
            }
        }
    }

    private void configurarJogoInicial(int numberTeams, int numberPlayers) {
        String gameId = "game" + roomCounter;

        System.out.println(" \n Código da Sala: " + gameId);
        System.out.println(" Configuração: " + numberTeams + " equipas de " + numberPlayers + " jogadores.");

        Game game = new Game(gameId, numberTeams, numberPlayers);
        GameState gameState = new GameState(game);

        // Synchronize access to shared maps
        synchronized (clientsByGame) {
            clientsByGame.put(gameState, new ArrayList<>());
        }

        roomCounter++;
    }

    // Inner class to handle individual clients
    private class DealWithClient extends Thread {
        private Socket connection;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public DealWithClient(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                setStreams();
                serve();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void serve() throws IOException, ClassNotFoundException {
            while (true) {
                Object obj;
                try {
                    obj = in.readObject();

                    if (obj instanceof ConnectionMessage) {
                        ConnectionMessage msg = (ConnectionMessage) obj;
                        System.out.println("Login attempt: " + msg.getClientCode() + " for " + msg.getGameCode());

                        // Find the game they want to join
                        GameState targetGameState = null;
                        synchronized (clientsByGame) {
                            for (GameState gs : clientsByGame.keySet()) {
                                if (gs.getGame().getGameCode().equals(msg.getGameCode())) {
                                    targetGameState = gs;
                                    break;
                                }
                            }
                        }

                        if (targetGameState != null) {
                            // Try to add player to game logic
                            if (targetGameState.getGame().addPlayer(msg.getTeamCode(), msg.getClientCode())) {

                                // Add this thread to the list of clients for that game
                                synchronized (clientsByGame) {
                                    clientsByGame.get(targetGameState).add(this);
                                }
                                System.out.println("Player joined " + msg.getGameCode());

                                // Here you would normally enter the main loop for this specific client
                                // playGameLoop(targetGame);
                                targetGameState.getGame().startGame();
                            } else {
                                System.out.println("Game full or invalid team.");
                                connection.close();
                            }
                        } else {
                            System.out.println("Game ID not found.");
                            connection.close();
                        }
                    } else if (obj instanceof CurrentQuestion) {
                        CurrentQuestion resposta = (CurrentQuestion) obj;

                        if (resposta.getSelectedAnswerIndex() != null) {
                            System.out.println("O cliente respondeu: " + resposta.getSelectedAnswerIndex());
                            // Processar pontuação...
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {}
            }
        }

        public void sendQuestion(Question q) throws IOException {
            // Cria o pacote com a pergunta e opções
            CurrentQuestion currentQuestion = new CurrentQuestion(q.getQuestion(), q.getOptions());

            // O campo 'selectedAnswerIndex' segue vazio (null)
            out.writeObject(currentQuestion);
            out.flush();
        }

        private void setStreams() throws IOException {
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
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

    }

}