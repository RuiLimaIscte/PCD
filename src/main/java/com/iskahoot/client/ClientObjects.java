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
            sendConnectionMessage(game, team, client);
            waitMessages();

        } catch (IOException e) {// ERRO ...
            e.printStackTrace();
        } finally {// a fechar ...
            closeCon();
        }
    }

    private void sendConnectionMessage(String game, String team, String client) {
        ConnectionMessage connectionMessage = new ConnectionMessage(game, team, client);
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

//    private void waitmensages() {
//        while (true) {
//            try {
//                Object obj = in.readObject();
//                System.out.println("Mensagem recebida pelo cliente: " + obj);
//                if (obj instanceof QuestionMessage) {
//                    System.out.println("Tipo: CurrentQuestion recebida pelo cliente");
//                    QuestionMessage questionMessage = (QuestionMessage) obj;
//
//                    if(clientGUI == null) {
//                        clientGUI = new SimpleClientGUI(questionMessage, "Client 1", (answer) -> {
//                            try {
//                                out.writeObject(answer);
//                                out.flush();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        });
//                    }
////                    } else {
////                        clientGUI.di(questionMessage, "Client 1", (answer) -> {
////                            try {
////                                out.writeObject(answer);
////                                out.flush();
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        });
////                    }
//                }
////                else if (obj instanceof TimeMessage) {
////                    TimeMessage timeMessage = (TimeMessage) obj;
////                    long serverTime = timeMessage.getCurrentTimeMillis();
////                    System.out.println("Hora do servidor: " + serverTime);
////                    sendConfirmation();
////
////                    //TODO  mudar isto para receber info do servidor e nao crirar um quiz no Client
////                    Quiz quiz = new Quiz(loadFromFile("src/main/resources/questions.json").getName(),
////                            loadFromFile("src/main/resources/questions.json").getQuestions());
////
////                    clientGUI = new SimpleClientGUI(quiz.getQuestions(), "Client 1");
////                    System.out.println(timeMessage.getTimeToEndRound());
////                    countdownInGUI((int) timeMessage.getTimeToEndRound());
////
////                    sendAnswer();
////                }
//            } catch (EOFException e) {
//                System.out.println("Connection closed by server.");
//                break;
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
                        String playerInfo = player.getUsername() + " " + player.getTeamCode() + " " + player.getGameCode();
                        clientGUI = new SimpleClientGUI(questionMessage, player , myAnswerListener);
                    } else {
                        System.out.println("Atualizando GUI existente...");
                        clientGUI.updateQuestion(questionMessage);
                    }
                }
                // ... (outros ifs para TimeMessage, etc)

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




    void sendConfirmation() throws IOException, ClassNotFoundException {
        ReceptionConfirmationMessage receptionConfirmationMessage = new ReceptionConfirmationMessage(System.currentTimeMillis());
        out.writeObject(receptionConfirmationMessage);
        out.flush();
        System.out.println("Confirmação enviada!");
//        long serverSentAt = timeMessage.getCurrentTimeMillis();
//        long clientReceivedAt = receptionConfirmationMessage.getReceivedAtMillis();
//        long latency = clientReceivedAt - serverSentAt;
//        displayedTime = serverSentAt - latency;

//        try {
//                Thread.sleep ( 3000 );
//            } catch ( InterruptedException e ) {
//                e . printStackTrace ();
//            }
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
