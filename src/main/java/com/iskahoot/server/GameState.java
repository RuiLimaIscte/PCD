package com.iskahoot.server;

import com.iskahoot.common.models.Question;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameState extends Thread {

    private final Game game; // Instância dos DADOS
    private final List<GameServer.DealWithClient> connectedClients; // Lista para enviar mensagens

    public GameState(Game game) {
        this.game = game;
        this.connectedClients = new ArrayList<>();
    }

    public Game getGame() {
        return game;
    }

    // Método chamado pelo Server quando alguém se conecta
    public synchronized boolean registerPlayer(String teamCode, String clientCode, GameServer.DealWithClient clientThread) {
        if (game.isGameFull()) return false;

        game.addPlayer(teamCode, clientCode);

        connectedClients.add(clientThread);

        System.out.println("Jogador adicionado. Total: " + connectedClients.size());

        // 3. Se encheu, ACORDA a thread deste jogo (o método run em baixo)
        if (game.isGameFull()) {
            notifyAll();
        }
        return true;
    }

    @Override
    public void run() {
        // --- FASE 1: ESPERA (WAITING) ---
        synchronized (this) {
            try {
                while (!game.isGameFull()) {
                    System.out.println("Sala " + game.getGameCode() + " aguardando jogadores...");
                    wait(); // Fica aqui parado até o registerPlayer chamar notifyAll()
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        System.out.println("Sala cheia! Iniciando o jogo " + game.getGameCode());
        game.setStatus(Game.STATUS.PLAYING);

        try {
            // Loop pelas perguntas do Quiz (que está na classe Game)
            for (Question q : game.getQuiz().getQuestions()) {

                // Envia a pergunta para TODOS
                broadcastQuestion(q);

                // Espera X segundos para eles responderem
                // (Futuramente podes trocar isto por outro wait() que espera respostas)
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Jogo Terminado.");
        game.setStatus(Game.STATUS.FINISHED);
    }

    private synchronized void broadcastQuestion(Question q) {
        System.out.println("Enviando pergunta: " + q.getQuestion());
        for (GameServer.DealWithClient client : connectedClients) {
            try {
                client.sendQuestion(q);
            } catch (IOException e) {
                System.out.println("Erro ao enviar para um cliente (talvez desconectou).");
            }
        }
    }
}