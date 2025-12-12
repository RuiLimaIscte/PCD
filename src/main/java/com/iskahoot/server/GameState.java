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

    // regista um jogador na sala
    public synchronized boolean registerPlayer(String teamCode, String clientCode, GameServer.DealWithClient clientThread) {
        if (game.isGameFull()) return false;

        boolean added = game.addPlayer(teamCode, clientCode);
        if (!added) {
            System.out.println("Jogador " + clientCode + " rejeitado, equipa cheio");
            return false;
        }

        connectedClients.add(clientThread);

        System.out.println("Jogador adicionado, total: " + connectedClients.size());

        //Se esta cheio, acorda a thread deste jogo
        if (game.isGameFull()) {
            notifyAll();
        }
        return true;
    }


    @Override
    public void run() {
        synchronized (this) {
            try {
                while (!game.isGameFull()) {
                    System.out.println("Sala " + game.getGameCode() + " aguardando jogadores");
                    // Fica aqui até o registerPlayer chamar notifyAll()
                    wait();
                }
            } catch (InterruptedException e) {
                return;
            }
        }

        System.out.println("Sala cheia. Iniciar o jogo " + game.getGameCode());
//        game.setStatus(Game.STATUS.PLAYING);

        try {
            for (Question q : game.getQuiz().getQuestions()) {

                // Guardar uma cópia do mapa atual para comparar no fim
                Map<String, Integer> scoresBeforeRound = game.getTeamScoresMap();

                currentQuestionIndex++;
                System.out.println("Pergunta " + currentQuestionIndex);

                // Limpar respostas da ronda anterior
                synchronized (this) {
                    playersWhoAnswered.clear();
                    // Inicializa Latch ou Barriers
                    prepareRoundSyncTools(q);
                }

                broadcastQuestion(q);

                synchronized (this) {
                    //broadcastTime(System.currentTimeMillis(), ROUNDTIME);
                    // Se alguém fizer notifyAll(), ele acorda antes dos 30s
                    // Se ninguém fizer nada, ele acorda sozinho passados 30s
                    //TODO passar para o latch/barrier???
                    //e receber respostas com o time que o client respondeu
                    wait(ROUNDTIME);
                }

                //Se é do tipo team e existe barreiras mas a ronda acabou, forçar o fim das barreiras
                if (q.isTeam() && teamBarriers != null) {
                    teamBarriers.values().forEach(TeamBarrier::forceFinish);
                }

                // Passar o mapa antigo para calcular a diferença
                broadcastScoreboard(currentQuestionIndex + 1, game.getQuiz().getQuestions().size(), scoresBeforeRound);

                System.out.println("Tempo esgotado ou todos responderam. Próxima pergunta");
                //tempo para ver scoreboard
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Jogo Terminado.");
//        game.setStatus(Game.STATUS.FINISHED);
    }

    private void prepareRoundSyncTools(Question q) {
        if (q.isIndividual()) {
            // Bonus vezes 2, para os primeiros 2 jogadores se acertarem ambos
            int totalPlayers = game.getConnectedPlayersCount();
            individualLatch = new ModifiedCountdownLatch(2, 2, totalPlayers);
            teamBarriers = null;
        } else {
            // Team: Criar uma barreira por equipa
            individualLatch = null;
            teamBarriers = new HashMap<>();

            // Para cada equipa, criamos uma barreira e uma thread à espera dela
            for (Team t : game.getTeams()) {
                TeamBarrier barrier = new TeamBarrier(t.getPlayerCount());
                teamBarriers.put(t.getTeamCode(), barrier);

                // Criar uma Thread que fica à espera desta equipa específica
                new Thread(() -> {
                    try {
                        System.out.println("Monitor da equipa " + t.getTeamCode() + " à espera...");

                        //thread bloqueia
                        barrier.await();

                        // Acorda quando a barreira faz notifyAll()
                        System.out.println("Equipa " + t.getTeamCode() + " desbloqueada!");

                        // calcula e atribui pontos
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

    public synchronized void receiveAnswer(String clientCode, int answerIndex) {
        if (currentQuestionIndex >= game.getQuiz().getQuestions().size()) return;

        Question currentQ = game.getQuiz().getQuestion(currentQuestionIndex);
        // Registar que este cliente respondeu
        playersWhoAnswered.add(clientCode);
        System.out.println("Resposta recebida de " + clientCode + ". Total: " + playersWhoAnswered.size());

        //int correctAnswerIndex = game.getQuiz().getQuestion(currentQuestionIndex).getCorrect();
        //Atualiza o objeto player com a pontuação
        //Player player = findPlayer(clientCode);
        //calculateScore(player, answerIndex, correctAnswerIndex);
        Player player = findPlayer(clientCode);
        if (player == null) {
            System.out.println("Player errado");
            return;
        }

        if (currentQ.isIndividual()) {
            processIndividualAnswer(player, answerIndex, currentQ);
        } else {
            processTeamAnswer(player, answerIndex);
        }

        // Verificar se todos já responderam
        if (playersWhoAnswered.size() >= connectedClients.size()) {
            System.out.println("Todos responderam");
            //desbloqueia a thread principal do jogo
            notifyAll();
        }

    }

    // Lógica para Pergunta Individual
    private void processIndividualAnswer(Player player, int answerIndex, Question q) {
        if (individualLatch == null) return;

        // O countdown retorna o multiplicador (2 se for o mais rápido, 1 normal)
        int multiplier = individualLatch.countdown();
        int correctIndex = q.getCorrect();

        if (answerIndex == correctIndex) {
            int points = q.getPoints() * multiplier;
            player.addScore(points);
            // adicionamos à equipa (soma dos membros)
            Team t = game.getTeam(player.getTeamCode());
            if (t != null) t.addScore(points);

            System.out.println("Individual: " + player.getPlayerCode() + " ganhou " + points + " pontos (x" + multiplier + ")");
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

    // Atualizamos a assinatura para receber 'scoresBeforeRound'
    private synchronized void broadcastScoreboard(int currentRound, int totalRounds, Map<String, Integer> scoresBeforeRound) {
        System.out.println("Enviando Scoreboard");

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


    private synchronized void broadcastQuestion(Question q) {
        System.out.println("Enviada pergunta: " + q.getQuestion());
        for (GameServer.DealWithClient client : connectedClients) {
            try {
                client.sendQuestion(q, System.currentTimeMillis(), ROUNDTIME);
            } catch (IOException e) {
            }
        }
    }
//    private synchronized void broadcastTime(long timestamp, int roundTime) {
//        System.out.println("Enviado time: " + timestamp);
//        for (GameServer.DealWithClient client : connectedClients) {
//            try {
//                client.sendTime(timestamp, roundTime);
//            } catch (IOException e) {
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
}