package com.iskahoot.server;

import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Team;

import java.io.IOException;
import java.security.Timestamp;
import java.util.*;

public class GameState extends Thread {

    private static final int ROUNDTIME = 90000;

    private final Game game; // Instância dos DADOS
    private final List<GameServer.DealWithClient> connectedClients;
    private final Set<String> playersWhoAnswered = new HashSet<>();// Lista para enviar mensagens
    private int currentQuestionIndex = -1;

    private ModifiedCountdownLatch individualLatch;
    private Map<String, TeamBarrier> teamBarriers; // Key: TeamCode


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

                // 1. CAPTURAR PONTUAÇÕES ANTES DA RONDA
                // Guardamos uma cópia do mapa atual para comparar no fim
                Map<String, Integer> scoresBeforeRound = game.getTeamScoresMap();

                currentQuestionIndex++;
                System.out.println("Pergunta " + currentQuestionIndex);
                //TODO se for a ultima pergunta fazer diferente

                // Limpar respostas da ronda anterior
                synchronized (this) {
                    playersWhoAnswered.clear();
                    //NOVO
                    prepareRoundSyncTools(q); // Inicializa Latch ou Barriers
                }

                broadcastQuestion(q);

                synchronized (this) {
                    broadcastTime(System.currentTimeMillis(), ROUNDTIME);
                    // Se alguém fizer notifyAll(), ele acorda antes dos 30s
                    // Se ninguém fizer nada, ele acorda sozinho passados 30s
                    wait(ROUNDTIME);
                }
                // Fim da ronda: processar pontuações pendentes de equipa (caso o tempo tenha acabado)
//                if (q.isTeam()) {
//                    finalizeTeamScores(q);
//                }
                if (q.isTeam() && teamBarriers != null) {
                    teamBarriers.values().forEach(TeamBarrier::forceFinish);
                }

                // --- NOVO: Enviar Scoreboard Atualizado ---
                // 2. ENVIAR SCOREBOARD COM A DIFERENÇA
                // Passamos o mapa antigo para calcularmos a diferença
                broadcastScoreboard(currentQuestionIndex + 1, game.getQuiz().getQuestions().size(), scoresBeforeRound);

                System.out.println("Tempo esgotado ou todos responderam. Próxima pergunta...");
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Jogo Terminado.");
        game.setStatus(Game.STATUS.FINISHED);
    }

    // Atualizamos a assinatura para receber 'scoresBeforeRound'
    private synchronized void broadcastScoreboard(int currentRound, int totalRounds, Map<String, Integer> scoresBeforeRound) {
        System.out.println("Enviando Scoreboard...");

        // Pontuações atuais (Pós-ronda)
        Map<String, Integer> currentScores = game.getTeamScoresMap();

        // Mapa para guardar apenas o que ganharam nesta ronda
        Map<String, Integer> lastRoundScores = new HashMap<>();

        // Calcular a diferença: Atual - Antigo
        for (Map.Entry<String, Integer> entry : currentScores.entrySet()) {
            String teamCode = entry.getKey();
            int currentVal = entry.getValue();
            int oldVal = scoresBeforeRound.getOrDefault(teamCode, 0);

            lastRoundScores.put(teamCode, currentVal - oldVal);
        }

        ScoreboardData sbData = new ScoreboardData(currentScores, lastRoundScores, currentRound, totalRounds);

        for (GameServer.DealWithClient client : connectedClients) {
            try {
                client.sendScoreboard(sbData);
            } catch (IOException e) {
                System.out.println("Erro ao enviar scoreboard.");
            }
        }
    }

    private void prepareRoundSyncTools(Question q) {
        if (q.isIndividual()) {
            // Latch: Bónus x2, para os primeiros 2, tempo ROUNDTIME, total de jogadores
            int totalPlayers = game.getConnectedPlayersCount();
            individualLatch = new ModifiedCountdownLatch(2, 2, ROUNDTIME, totalPlayers);
            teamBarriers = null;
        } else {
            // Team: Criar uma barreira por equipa
            individualLatch = null;
            teamBarriers = new HashMap<>();

            // --- MUDANÇA PRINCIPAL ---
            // Para cada equipa, criamos uma barreira E uma thread à espera dela
            for (Team t : game.getTeams()) {
                TeamBarrier barrier = new TeamBarrier(t.getPlayerCount());
                teamBarriers.put(t.getTeamCode(), barrier);

                // Criar uma Thread que fica à espera (wait) desta equipa específica
                new Thread(() -> {
                    try {
                        System.out.println("Monitor da equipa " + t.getTeamCode() + " à espera...");

                        // 1. A thread bloqueia aqui (WAIT)
                        barrier.await();

                        // 2. Acorda quando a barreira faz notifyAll() (ou forceFinish)
                        System.out.println("Equipa " + t.getTeamCode() + " desbloqueada!");

                        // 3. Calcula e atribui pontos
                        int totalTeamScore = barrier.calculateTeamScore(q.getCorrect(), q.getPoints());
                        if (totalTeamScore > 0) {
                            t.addScore(totalTeamScore);

                            int scorePerPlayer = totalTeamScore / t.getPlayerCount();
                            // Sincronizar para evitar concorrência na lista de players se necessário
                            synchronized (t) {
                                t.getPlayers().forEach(p -> p.addScore(scorePerPlayer));
                            }
                            System.out.println("Pontos atribuídos à equipa " + t.getTeamCode() + ": " + totalTeamScore);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
    //TODO score
    public synchronized void receiveAnswer(String clientCode, int answerIndex) {
        if (currentQuestionIndex >= game.getQuiz().getQuestions().size()) return;

        Question currentQ = game.getQuiz().getQuestion(currentQuestionIndex);
        // Registar que este cliente respondeu
        playersWhoAnswered.add(clientCode);
        System.out.println("Resposta recebida de " + clientCode + ". Total: " + playersWhoAnswered.size());

        //int correctAnswerIndex = game.getQuiz().getQuestion(currentQuestionIndex).getCorrect();
        //Atualiza o objeto player com a pontuação
//        Player player = findPlayer(clientCode);
//        calculateScore(player, answerIndex, correctAnswerIndex);
        Player player = findPlayer(clientCode);
        if (player == null) {
            System.out.println("Player not found");
            return;
        }

        if (currentQ.isIndividual()) {
            processIndividualAnswer(player, answerIndex, currentQ);
        } else {
            processTeamAnswer(player, answerIndex);
        }

        // Verificar se TODOS já responderam
        if (playersWhoAnswered.size() >= connectedClients.size()) {
            System.out.println("Todos responderam! Avançando...");
            notifyAll();
        }

    }

    // Lógica para Pergunta Individual
    private void processIndividualAnswer(Player player, int answerIndex, Question q) {
        if (individualLatch == null) return;

        // O countdown retorna o multiplicador (2 se for rápido, 1 normal)
        int multiplier = individualLatch.countdown();
        int correctIndex = q.getCorrect();

        if (answerIndex == correctIndex) {
            int points = q.getPoints() * multiplier;
            player.addScore(points);
            // Também adicionamos à equipa (soma dos membros)
            Team t = game.getTeam(player.getTeamCode());
            if (t != null) t.addScore(points);

            System.out.println("Individual: " + player.getPlayerCode() + " ganhou " + points + " pts (x" + multiplier + ")");
        }
    }

    // Lógica para Pergunta de Equipa
    private void processTeamAnswer(Player player, int answerIndex) {
        if (teamBarriers == null) return;
        TeamBarrier barrier = teamBarriers.get(player.getTeamCode());
        if (barrier != null) {
            // Isto vai disparar o notifyAll() lá dentro se for o último
            barrier.registerAnswer(player.getPlayerCode(), answerIndex);
        }
    }

    private Player findPlayer(String clientCode) {
        // Como teams agora é uma List<Team>, iteramos diretamente
        for (Team t : game.getTeams()) {
            for (Player p : t.getPlayers()) { //
                if (p.getPlayerCode().equals(clientCode)) return p; //
            }
        }
        return null;
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
    private synchronized void broadcastTime(long timestamp, int roundTime) {
        System.out.println("Enviando time: " + timestamp);
        for (GameServer.DealWithClient client : connectedClients) {
            try {
                client.sendTime(timestamp, roundTime);
            } catch (IOException e) {
                System.out.println("Erro ao enviar para um cliente (talvez desconectou).");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}