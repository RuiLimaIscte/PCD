package com.iskahoot.server;

import com.iskahoot.common.models.Quiz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;


public class Game {
    public enum STATUS {
        WAITING,    // Waiting for players
        READY,      // All players connected
        PLAYING,    // Game in progress
        FINISHED    // Game ended
    }

    private final String gameCode;
    private final Quiz quiz;
    private final int numberOfTeams;
    private final int playersPerTeam;
    private Map<String, Set<String>> playersByTeam;
    private int currentQuestionIndex;
    private STATUS status;

    public Game(String gameCode, int numberOfTeams, int playersPerTeam) {
        this.gameCode = gameCode;
        this.numberOfTeams = numberOfTeams;
        this.playersPerTeam = playersPerTeam;
        this.quiz = loadFromFile("src/main/resources/questions.json");
        this.playersByTeam = new HashMap<>();
        this.currentQuestionIndex = 0;
        this.status = STATUS.WAITING;
    }

    public synchronized void addPlayer(String teamCode, String playerCode) {
        playersByTeam.computeIfAbsent(teamCode, k -> new HashSet<>()).add(playerCode);

        // Check if room is ready to start
        if (isGameFull()) {
            status = STATUS.READY;
            System.out.println("Room " + gameCode + " is ready to start!");
        }
    }

    public synchronized boolean canStartGame() {
        if (status != STATUS.READY) {
            System.out.println("Room is not ready to start, waiting for players to join...");
            return false;
        }

        status = STATUS.PLAYING;

        System.out.println("Game starting in room: " + gameCode);

        return true;
    }

    public boolean isGameFull() {
        return getConnectedPlayers() >= getExpectedPlayers();
    }

    public String getGameCode() {
        return gameCode;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getExpectedPlayers() {
        return numberOfTeams * playersPerTeam;
    }

    public int getConnectedPlayers() {
        return playersByTeam.values().stream().mapToInt(Set::size).sum();
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
}

