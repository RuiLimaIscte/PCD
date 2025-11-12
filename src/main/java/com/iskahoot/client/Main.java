package com.iskahoot.client;

import com.iskahoot.common.models.Quiz;
import com.iskahoot.common.models.Player;
import com.iskahoot.utils.QuestionLoader;
import com.iskahoot.server.Room;
import com.iskahoot.server.GameState;

public class Main {
    public static void main(String[] args) throws Exception {
        Quiz quiz = QuestionLoader.loadFromResources("questions.json");

        Room room = new Room("DEMO", 1, quiz);
        Player player1 = new Player("You", "TEAM1");
        Player player2 = new Player("Bot", "TEAM1");
        room.addPlayer("TEAM1", player1);
        room.addPlayer("TEAM1", player2);

        room.startGame();
        GameState gameState = room.getGameState();
        gameState.nextRound();

        ClientGUI gui = new ClientGUI(quiz.getQuestions());

        gui.setAnswerCallback((questionIndex, answerIndex) -> {
            player1.setCurrentAnswer(answerIndex);
            player1.setHasAnswered(true);
            gui.updateScoreboard(gameState.endRound());
        });

        gui.setNextQuestionCallback((newQuestionIndex) -> {
            gameState.nextRound();
        });

        gui.updateInitialScoreboard(gameState.getTotalQuestions());
        gui.resetForNextQuestion();
        Thread.sleep(1000);
        gui.showNextQuestion();
    }
}
