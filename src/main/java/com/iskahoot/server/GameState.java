package com.iskahoot.server;

import com.iskahoot.common.models.Question;
import com.iskahoot.common.models.Quiz;

import java.util.List;

/**
 * Manages the state of an active game
 * Separated into components to avoid unnecessary blocking
 */
//public class GameState {
//    private final Room room;
//    private final Quiz quiz;
//    private final List<Question> questions;
//
//    private int currentQuestionIndex;
//    private Question currentQuestion;
//    private volatile boolean roundActive;
//    private long roundStartTime;
//    private static final int ROUND_DURATION_SECONDS = 30;
//
//    public GameState(Room room, Quiz quiz) {
//        this.room = room;
//        this.quiz = quiz;
//        this.questions = quiz.getQuestions();
//        this.currentQuestionIndex = -1;
//        this.roundActive = false;
//    }
//
//    /**
//     * Start next round
//     * @return true if there is a next question, false if game is over
//     */
//    public synchronized boolean nextRound() {
//        currentQuestionIndex++;
//
//        if (currentQuestionIndex >= questions.size()) {
//            return false;  // Game over
//        }
//
//        currentQuestion = questions.get(currentQuestionIndex);
//        roundActive = true;
//        roundStartTime = System.currentTimeMillis();
//
//        System.out.println("Round " + (currentQuestionIndex + 1) + "/" + questions.size());
//        System.out.println("Question: " + currentQuestion.getQuestion());
//
//        return true;
//    }
//
//    /**
//     * End current round
//     */
//    public synchronized void endRound() {
//        roundActive = false;
//        System.out.println("Round ended");
//
//        // TODO: Calculate scores
//        // TODO: Send scoreboard to all players
//    }
//
//    /**
//     * Check if round time has expired
//     */
//    public boolean isRoundExpired() {
//        if (!roundActive) {
//            return false;
//        }
//
//        long elapsed = (System.currentTimeMillis() - roundStartTime) / 1000;
//        return elapsed >= ROUND_DURATION_SECONDS;
//    }
//
//    /**
//     * Get remaining time in seconds
//     */
//    public int getRemainingTime() {
//        if (!roundActive) {
//            return 0;
//        }
//
//        long elapsed = (System.currentTimeMillis() - roundStartTime) / 1000;
//        return Math.max(0, ROUND_DURATION_SECONDS - (int)elapsed);
//    }
//
//    public Question getCurrentQuestion() {
//        return currentQuestion;
//    }
//
//    public int getCurrentQuestionIndex() {
//        return currentQuestionIndex;
//    }
//
//    public int getTotalQuestions() {
//        return questions.size();
//    }
//
//    public boolean isRoundActive() {
//        return roundActive;
//    }
//
//    public Room getRoom() {
//        return room;
//    }
//}

