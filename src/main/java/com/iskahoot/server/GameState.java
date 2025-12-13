package com.iskahoot.server;

import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;


public class GameState extends Thread {

    //30 segundos
    private static final int ROUNDTIME = 30000;

    private final Game game;
    private final List<GameServer.DealWithClient> connectedClients;
    private final Set<String> playersWhoAnswered = new HashSet<>();
    private int currentQuestionIndex = -1;

    private ModifiedCountdownLatch individualLatch;
    private Map<String, TeamBarrier> teamBarriers; // Key: TeamCode


    public GameState(Game game) {
        this.game = game;
        this.connectedClients = new ArrayList<>();
    }

    // regista um jogador na sala
    public synchronized boolean registerPlayer(String teamCode, String clientCode, GameServer.DealWithClient clientThread) {
        if (game.isGameFull()) return false;

        boolean added = game.addPlayer(teamCode, clientCode);
        if (!added) {
            System.out.println("Jogador " + clientCode + " rejeitado, equipa cheio");
            return false;
        }
        connectedClients.add(clientThread);

        //System.out.println("Jogador adicionado, total: " + connectedClients.size());

        //Se esta cheio, acorda a thread deste jogo
        if (game.isGameFull())
            notifyAll();

        return true;
    }


    @Override
    public void run() {
        synchronized (this) {
            try {
                while (!game.isGameFull()) {
                    //System.out.println("Sala " + game.getGameCode() + " à espera de jogadores");
                    // espera até o registerPlayer chamar notifyAll()
                    wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }
       // System.out.println("Começar o jogo " + game.getGameCode());
        try {
            for (Question question : game.getQuiz().getQuestions()) {

                // scores antigos
                Map<String, Integer> scoresBeforeRound = game.getTeamScoresMap();

                currentQuestionIndex++;
                System.out.println("Pergunta " + currentQuestionIndex);

                // limpar respostas da ronda anterior
                synchronized (this) {
                    playersWhoAnswered.clear();
                    // Latch ou Barriers
                    makeCountDownLatchOrBarrier(question);
                }

                broadcastQuestion(question);

                synchronized (this) {
                    wait(ROUNDTIME);
                }

                //se ainda existe barreiras mas a ronda acabou, notifica-las
                if (question.isTeam() && teamBarriers != null) {
                    teamBarriers.values().forEach(TeamBarrier::forceFinish);
                }

                broadcastScoreboard(currentQuestionIndex + 1, game.getQuiz().getQuestions().size(), scoresBeforeRound);

                //tempo para ver scoreboard
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Jogo acabou, voltem sempre :) ");
        for (GameServer.DealWithClient client : connectedClients) {
            client.closeConnection();
        }
    }

    private void makeCountDownLatchOrBarrier(Question question) {
        if (question.isIndividual()) {
            // Bonus vezes 2 para os primeiros 2 jogadores se acertarem ambos
            int totalPlayers = game.getConnectedPlayersCount();
            individualLatch = new ModifiedCountdownLatch(2, 2, totalPlayers);
            teamBarriers = null;
        } else {
            individualLatch = null;
            teamBarriers = new HashMap<>();

            for (Team team : game.getTeams()) {
                TeamBarrier barrier = new TeamBarrier(team.getPlayerCount());
                teamBarriers.put(team.getTeamCode(), barrier);

                // Thread para esta equipa
                new Thread(() -> {
                    try {
                        barrier.await();
                        //registerAnswer chamou notifyAll
                        //System.out.println("barreira " + team.getTeamCode() + " desbloqueada");

                        // calcula e atribui pontos
                        int totalTeamScore = barrier.calculateTeamScore(question.getCorrect(), question.getPoints());
                        if (totalTeamScore > 0) {
                            team.addScore(totalTeamScore);

                            int scorePerPlayer = totalTeamScore / team.getPlayerCount();

                            synchronized (team) {
                                team.getPlayers().forEach(p -> p.addScore(scorePerPlayer));
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void processIndividualAnswer(Player player, int answerIndex, Question q) {
        if (individualLatch == null) return;

        // O countdown retorna o multiplicador (2 se for o mais rápido, 1 normal)
        int multiplier = individualLatch.countdown();
        int correctIndex = q.getCorrect();

        if (answerIndex == correctIndex) {
            int points = q.getPoints() * multiplier;
            player.addScore(points);

            Team team = game.getTeam(player.getTeamCode());
            if (team != null) team.addScore(points);

            //System.out.println("Pergunta individual, player" + player.getPlayerCode() + " ganhou " + points + " pontos com multiplier " + multiplier);
        }
    }

    private void processTeamAnswer(Player player, int answerIndex) {
        if (teamBarriers == null) return;
        TeamBarrier barrier = teamBarriers.get(player.getTeamCode());
        if (barrier != null) {
            // notifyAll() se for o último
            barrier.registerAnswer(player.getPlayerCode(), answerIndex);
        }
    }

    public synchronized void receiveAnswer(String clientCode, int answerIndex) {
        if (currentQuestionIndex >= game.getQuiz().getQuestions().size()) return;

        Question currentQ = game.getQuiz().getQuestion(currentQuestionIndex);
        // Registar que este cliente respondeu
        playersWhoAnswered.add(clientCode);
       // System.out.println("Resposta recebida de " + clientCode);

        Player player = findPlayer(clientCode);
        if (player == null) {
            return;
        }

        if (currentQ.isIndividual()) {
            processIndividualAnswer(player, answerIndex, currentQ);
        } else {
            processTeamAnswer(player, answerIndex);
        }
        // Verificar se todos já responderam
        if (playersWhoAnswered.size() >= connectedClients.size()) {
            //System.out.println("Responderam todos");
            //desbloqueia a thread do jogo
            notifyAll();
        }

    }

    private Player findPlayer(String clientCode) {
        for (Team team : game.getTeams()) {
            for (Player player : team.getPlayers()) {
                if (player.getPlayerCode().equals(clientCode)) return player;
            }
        }
        return null;
    }

    private synchronized void broadcastScoreboard(int currentRound, int totalRounds, Map<String, Integer> scoresBeforeRound) {
        // pontuações atuais
        Map<String, Integer> currentScores = game.getTeamScoresMap();

        // guardar os pontos ganhos na ronda passada
        Map<String, Integer> lastRoundScores = new HashMap<>();

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
                e.printStackTrace();
            }
        }
    }

    private synchronized void broadcastQuestion(Question q) {
        for (GameServer.DealWithClient client : connectedClients) {
            try {
                client.sendQuestion(q, System.currentTimeMillis(), ROUNDTIME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}