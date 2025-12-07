package com.iskahoot.server;

import com.iskahoot.common.models.Question;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameState extends Thread {

    private final Game game; // Instância dos DADOS
    private final List<GameServer.DealWithClient> connectedClients;
    private final Set<String> playersWhoAnswered = new HashSet<>();// Lista para enviar mensagens

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

                // 1. Limpar respostas da ronda anterior (CRÍTICO!)
                synchronized (this) {
                    playersWhoAnswered.clear();
                }

                // 2. Enviar pergunta
                broadcastQuestion(q);

                // 3. Esperar: 30 segundos OU até notifyAll() ser chamado
                synchronized (this) {
                    // O wait solta o lock e espera.
                    // Se alguém fizer notifyAll(), ele acorda antes dos 30000ms.
                    // Se ninguém fizer nada, ele acorda sozinho passados 30000ms.
                    wait(30000);
                }

                System.out.println("Tempo esgotado ou todos responderam. Próxima pergunta...");

                // Opcional: Pequena pausa para verem o resultado antes da próxima
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Jogo Terminado.");
        game.setStatus(Game.STATUS.FINISHED);
    }
    public synchronized void receiveAnswer(String clientCode, int answerIndex) {
        // 1. Registar que este cliente respondeu
        playersWhoAnswered.add(clientCode);
        System.out.println("Resposta recebida de " + clientCode + ". Total: " + playersWhoAnswered.size());

        // 2. Verificar se TODOS já responderam
        if (playersWhoAnswered.size() >= connectedClients.size()) { // Assume que tens este método no Game
            System.out.println("Todos responderam! Avançando...");
            notifyAll(); // <--- ACORDA O LOOP (sai do wait)
        }
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