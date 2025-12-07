//package com.iskahoot.server;
//
//import com.iskahoot.common.messages.*;
//import com.iskahoot.common.models.Question;
//
//import java.io.*;
//import java.net.Socket;
//
//
//public class DealWithClient2 extends Thread {
//    private Socket connection;
//    private ObjectInputStream in;
//    private ObjectOutputStream out;
//    private GameState game;
//
//    //    public DealWithClient(Socket connection) {
////        this.connection = connection;
////    }
//    public DealWithClient2(Socket connection, GameState game) {
//        this.connection = connection;
//        this.game = game;
//    }
//
//
//    @Override
//    public void run() {
//        try {
//            setStreams();
//            sendTime();
//            sendQuestion(game.getCurrentQuestion());
//            serve();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            closeConnection();
//        }
//    }
//
//    private void serve() throws IOException {
//        while (true) {
//            Object obj;
//            try {
//                obj = in.readObject();
//                if (obj instanceof ReceptionConfirmationMessage) {
//                    ReceptionConfirmationMessage confirmation = (ReceptionConfirmationMessage) obj;
//                    System.out.println("Confirmação recebida do cliente: " + confirmation.getReceivedAtMillis());
//                }
//
//                else if (obj instanceof CurrentQuestion) {
//                    CurrentQuestion resposta = (CurrentQuestion) obj;
//
//                    if (resposta.getSelectedAnswerIndex() != null) {
//                        System.out.println("O cliente respondeu: " + resposta.getSelectedAnswerIndex());
//                        // Processar pontuação...
//                    }
//                }
////                else  if (obj instanceof AnswerFromClient) {
////                    AnswerFromClient answer = (AnswerFromClient) obj;
////                    System.out.println("Resposta recebida do cliente: " + answer.getSelectedOptionIndex());
////
////                    //TODO TESTE
//////                    ModifiedCountdownLatch latch = server.getGameState().getCurrentLatch();
//////                    int multiplier = latch.countdown(); // devolve 2, 1 ou 0
//////
//////                    if (multiplier == 0) {
//////                        // resposta fora do prazo -> ignorar (ou registar como late)
//////                    } else {
//////                        int questionPoints = server.getGameState().getCurrentQuestion().getPoints();
//////                        int earned = questionPoints * multiplier;
//////                        // regista scoredPlayerScore/score na estrutura do servidor (GameState)
//////                        server.getGameState().submitPlayerAnswer(playerId, answer, earned);
//////                    }
////
////                    //TODO END OF TESTE
////                }
//            } catch (IOException | ClassNotFoundException e) {
//
//            }
//        }
//    }
//    public void sendQuestion(Question q) throws IOException {
//        // Cria o pacote com a pergunta e opções
//        CurrentQuestion currentQuestion = new CurrentQuestion(q.getQuestion(), q.getOptions());
//
//        // O campo 'selectedAnswerIndex' segue vazio (null)
//        out.writeObject(currentQuestion);
//        out.flush();
//    }
//
//    void sendTime() throws IOException, ClassNotFoundException {
//        TimeMessage timeMessage = new TimeMessage(System.currentTimeMillis(),5);
//        out.writeObject(timeMessage);
////        ReceptionConfirmationMessage confirmation = (ReceptionConfirmationMessage) in.readObject();
////        System.out.println("Confirmação recebida do cliente: " + confirmation.getReceivedAtMillis());
//
//
////        for (int i = 0; i < 10; i ++) {
////            Mensagem message= new Mensagem(i,"ola");
////            out.writeObject(message);
////            Mensagem str = (Mensagem)in.readObject();
////            System.out.println ( str );
////            try {
////                Thread.sleep ( 3000 );
////            } catch ( InterruptedException e ) {
////                e . printStackTrace ();
////            }
////        }
////        out.writeObject(new Mensagem(-1,"FIM"));
//    }
//
//    private void closeConnection() {
//        try {
//            if (connection != null)
//                connection.close();
//            if (in != null)
//                in.close();
//            if (out != null)
//                out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setStreams() throws IOException {
//        out = new ObjectOutputStream(connection.getOutputStream());
//        out.flush();
//        in = new ObjectInputStream(connection.getInputStream());
//    }
//
//}
//
