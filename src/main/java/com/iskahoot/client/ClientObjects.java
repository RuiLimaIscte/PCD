package com.iskahoot.client;

import com.iskahoot.common.messages.*;
import com.iskahoot.common.models.Player;
import com.iskahoot.utils.AnswerListener;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientObjects {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private SimpleClientGUI clientGUI;
    private Player player;

    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String game = args[2];
        String team = args[3];
        String client = args[4];
        new ClientObjects().runClient(ip, port, game, team, client);
    }

    public void runClient(String ip, int port, String game, String team, String client) {
        player = new Player(client, team, game);

        try {
            connectToServer(ip, port);
            setStreams();
            sendConnectionMessage();
            waitMessages();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {// a fechar ...
            closeCon();
        }
    }

    private void sendConnectionMessage() {
        ConnectionMessage connectionMessage = new ConnectionMessage(player);
        try {
            out.writeObject(connectionMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void connectToServer(String ip, int port) throws IOException {
        InetAddress address = InetAddress.getByName(ip);
        System.out.println(" Ligacao a : " + address + " : " + port);
        socket = new Socket(address, port);
        System.out.println("socket: " + socket);

    }

    void sendAnswer() throws IOException, ClassNotFoundException {
        AnswerFromClient answerFromClient = new AnswerFromClient(clientGUI.getSelectedOption());
        out.writeObject(answerFromClient);
        System.out.println("Resposta enviada: " + answerFromClient.getSelectedOptionIndex());
    }

    private void waitMessages() {
       AnswerListener myAnswerListener = (answerMsg) -> {
            try {
                out.writeObject(answerMsg);
                out.flush();
                System.out.println("Resposta enviada para a pergunta: " + answerMsg.getQuestionText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        while (true) {
            try {
                Object obj = in.readObject();
                System.out.println("Mensagem recebida pelo cliente: " + obj.getClass().getSimpleName());

                if (obj instanceof QuestionMessage) {
                    QuestionMessage questionMessage = (QuestionMessage) obj;

                    if (clientGUI == null) {
                        System.out.println("Criando nova GUI...");
                        clientGUI = new SimpleClientGUI(questionMessage, player , myAnswerListener);
                    } else {
                        System.out.println("Atualizando GUI existente...");
                        clientGUI.updateQuestion(questionMessage);
                    }
                }
                else if (obj instanceof TimeMessage) {
                    TimeMessage timeMessage = (TimeMessage) obj;

                    int totalSeconds = (int) (timeMessage.getTimeToEndRound() / 1000);//30
                    long timeNow = System.currentTimeMillis();
                    long latencyMillis = timeNow - timeMessage.getCurrentTimeMillis();
                    int latencySeconds = (int) (latencyMillis / 1000);

                    int finalTime = totalSeconds - latencySeconds;
                    System.out.println("Time left: " + finalTime);

                    if (clientGUI != null) {
                        clientGUI.startTimer(finalTime);
                    }
                }
                else if (obj instanceof ScoreboardData) {
                    ScoreboardData sbData = (ScoreboardData) obj;
                    System.out.println("Scoreboard recebido: " + sbData);

                    if (clientGUI != null) {
                        clientGUI.updateScoreboard(sbData);
                    }
                }


            } catch (EOFException e) {
                System.out.println("Connection closed by server.");
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    //TODO isto vai ser bloqueante???
    public void countdownInGUI(int seconds) {
        for (int i = seconds; i >= 0; i--) {
            System.out.println("Time left: " + i);
            clientGUI.updateTimerLabel(i);
            try {
                Thread.sleep(1000); // espera 1 segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.println("Time has run out!");
    }

    private void setStreams() throws IOException {
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

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


}
