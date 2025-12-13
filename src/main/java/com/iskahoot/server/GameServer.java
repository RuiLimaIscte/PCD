package com.iskahoot.server;

import com.iskahoot.common.messages.ConnectionMessage;
import com.iskahoot.common.messages.QuestionMessage;
import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Question;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GameServer {
    private final int port = 8080;
    private int roomCounter = 0;

    private Map<String, GameState> activeGames = new HashMap<>();

     static void main(String[] args) throws IOException {
        new GameServer().startServing();
    }

    public void startServing() throws IOException {
        System.out.println("Server started on port " + port);

        // Thread para criar jogos na consola
        new Thread(this::handleConsoleCommands).start();

        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new DealWithClient(socket, activeGames).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if (scanner.next().equalsIgnoreCase("new")) {
                int nTeams = scanner.nextInt();
                int nPlayers = scanner.nextInt();
                int nQuestions = scanner.nextInt();

                String code = "game" + roomCounter++;

                Game game = new Game(code, nTeams, nPlayers,nQuestions);
                GameState gameState = new GameState(game);

                synchronized (activeGames) {
                    activeGames.put(code, gameState);
                }
                gameState.start();

                System.out.println("Sala criada: " + code);
            }
        }
    }

    public static class DealWithClient extends Thread {
        private final Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private final Map<String, GameState> games;
        private GameState gameState;

        public DealWithClient(Socket socket, Map<String, GameState> games) {
            this.socket = socket;
            this.games = games;
        }

        @Override
        public void run() {
            try {
                setStreams();

                Object obj = in.readObject();

                if (obj instanceof ConnectionMessage) {
                    ConnectionMessage msg = (ConnectionMessage) obj;
                    synchronized (games) {
                        gameState = games.get(msg.getGameCode());
                        games.put(msg.getGameCode(), gameState);
                    }

                    if (gameState != null) {
                        boolean accepted = gameState.registerPlayer(msg.getTeamCode(), msg.getClientCode(), this);

                        if (accepted) {
                            System.out.println("Client conectado ao jogo  " + msg.getGameCode());
                            listenForAnswers();
                        } else {
                            //jogo cheio
                            closeConnection();
                        }
                    } else {
                        System.out.println("Jogo nao existe");
                        closeConnection();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setStreams() throws IOException {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }


        private void listenForAnswers() throws IOException, ClassNotFoundException {
            while (true) {
                Object obj = in.readObject();

                if (obj instanceof QuestionMessage) {
                    QuestionMessage resposta = (QuestionMessage) obj;
                    System.out.println("Resposta: " + resposta.getSelectedAnswerIndex() + " de: " + resposta.getClientCode());

                    gameState.receiveAnswer(resposta.getClientCode(), resposta.getSelectedAnswerIndex());
                }
            }
        }

        public void sendQuestion(Question q, long currentTimeMillis, int timeToEndRound) throws IOException {
            out.writeObject(new QuestionMessage(q.getQuestion(),q.getType(), q.getOptions(), currentTimeMillis, timeToEndRound));
            out.flush();
        }

        public void sendScoreboard(ScoreboardData data) throws IOException {
            out.writeObject(data);
            out.flush();
        }

        public void closeConnection() {
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
    }
}