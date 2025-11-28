package com.iskahoot.server;

import com.iskahoot.common.messages.ScoreboardData;
import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.common.models.Team;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameState {
    private  Room room;
    private  List<Question> questions;
    private ScoreboardData lastScoreboard; //cache do ultimo scoreboard
    private int currentQuestionIndex;
    private Question currentQuestion;
    private volatile boolean roundActive;
    private long roundStartTime;
    private static final int ROUND_DURATION_SECONDS = 30;
    //TODO para teste
    private Quiz quiz;

    //private final java.util.Map<String, Integer> playerAnswers = new java.util.HashMap<>();     O nosso player guarda esta informação
    //private final java.util.List<String> answerOrder = new java.util.ArrayList<>();             no entanto pode vir a dar jeito


    public GameState(Room room) {
        this.room = room;
        this.questions = room.getQuiz().getQuestions();
        this.currentQuestionIndex = -1;
        this.roundActive = false;
    }

    public GameState(Quiz quiz) {
        this.quiz = quiz;
        this.questions = quiz.getQuestions();
        this.currentQuestionIndex = -1;
    }
    //TODO TESTE
//    public void startGame() {
//        for (Question q : questions) {
//            SendQuestion(q);
//            latch.await();       // espera respostas ou timeout
//            processAnswers();
//            SendScoreboard();
//        }
//    }
    //TODO FIM DE TESTE

    public synchronized boolean nextRound() {
        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.size()) {
            System.out.println("Game Over!"); // isto e para debug
            return false;  // TODO: No futuro criar lógica para a ordem das perguntas ser aleatório
        }

        currentQuestion = questions.get(currentQuestionIndex);
        //Fazemos o reset de todos os parametros de ronda (RoundActive, Players e Scoreboard)
        roundActive = true;
        roundStartTime = System.currentTimeMillis();
        lastScoreboard = null; // Limpa-mos o cache do scoreboard para combater o problema do endRound, evitando que seja enviado uma scoreboard antiga

        for (Player p : room.getPlayers().values()) {
            p.setHasAnswered(false);
            p.setCurrentAnswer(null);
            System.out.println("Players reset for new round"); // isto e para debug
        }

        System.out.println("Round " + (currentQuestionIndex + 1) + "/" + questions.size());
        System.out.println("Question: " + currentQuestion.getQuestion());

        return true;
    }

    public synchronized ScoreboardData endRound() {

        if (!roundActive) {
            if (lastScoreboard != null) {
                return lastScoreboard;
                // Como o endRound pode ser chamado varias vezes, evitamos que seja enviado uma scoreboard antiga com a cache
                // isto porque uma segunda chamada do endRound antes de o roundActive voltar a ser true ia enviar uma scoreboard antiga
            } else {
                return buildScoreboard(new HashMap<>());
            }
        }

        roundActive = false;
        System.out.println("Round ended");

        Map<String, Integer> roundScores = new HashMap<>();
        if (currentQuestion != null) {
            int correctAnswer = currentQuestion.getCorrect();
            int basePoints = currentQuestion.getPoints();
            roundScores = calculateIndividualScores(correctAnswer, basePoints);
        }

        ScoreboardData scoreboard = buildScoreboard(roundScores);
        lastScoreboard = scoreboard;

        return scoreboard;
    }




    private Map<String, Integer> calculateIndividualScores(int correctAnswer, int basePoints) {
            Map<String, Integer> roundScores = new HashMap<>();
            int correctCount = 0;
            // TODO: Implementar ordem de resposta dos players para já está aleatório

            for (Player p : room.getPlayers().values()) {
                if (p.hasAnswered() && p.getCurrentAnswer() != null) {
                    if (p.getCurrentAnswer() == correctAnswer) {
                        // A ordem de resposta dos players não está implementada, por isso a ordem de resposta é baseada na ordem de iteração
                        int points = (correctCount < 2) ? basePoints * 2 : basePoints;

                        p.addScore(points);  // Update dos pontos
                        // TODO: Fazer o calculo correto da leaderboard
                        Team team = room.getTeams().get(p.getTeamCode()); // Neste momento só está a somar os pontos de cada player quando implementar-mos a barrier e o semaforo
                        if (team != null) {                               // o calculo da leaderboard terá provavelmente de passar para uma classe própria
                            team.addScore(points);
                        }

                        roundScores.put(p.getUsername(), points);
                        correctCount++;
                    } else {
                        roundScores.put(p.getUsername(), 0);
                    }
                } else { // Se o player não tiver respondido, não adiciona pontos, mas aparece no scoreboard
                    roundScores.put(p.getUsername(), 0);
                }
            }
            return roundScores;
        }

    private ScoreboardData buildScoreboard(Map<String, Integer> roundScores) {
        Map<String, Integer> totalScores = new HashMap<>();

        for (Team t : room.getTeams().values()) {
            totalScores.put(t.getTeamCode(), t.getTeamScore());
        }

        int roundNumber = currentQuestionIndex + 1;
        int totalRounds = questions.size();

        return new ScoreboardData(totalScores, roundScores, roundNumber, totalRounds);
    }


    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public Room getRoom() {
        return room;
    }
}

