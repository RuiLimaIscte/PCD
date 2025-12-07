package com.iskahoot.server;

import com.iskahoot.common.models.Quiz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.iskahoot.utils.QuestionLoader.loadFromFile;


public class Game {
    public enum RoomState {
        WAITING,    // Waiting for players
        READY,      // All players connected
        PLAYING,    // Game in progress
        FINISHED    // Game ended
    }

    private final String gameCode;
    private final Quiz quiz;
    private final int numberOfTeams;
    private final int playersPerTeam;
    private final Map<String, Set<String>> playersByTeam;
    private RoomState state;

    public Game(String gameCode, int numberOfTeams, int playersPerTeam) {
        this.gameCode = gameCode;
        this.numberOfTeams = numberOfTeams;
        this.playersPerTeam = playersPerTeam;
        this.quiz = loadFromFile("src/main/resources/questions.json");
        this.playersByTeam = new HashMap<>();
        this.state = RoomState.WAITING;
    }

    public synchronized boolean addPlayer(String teamCode, String playerCode) {
        // Check if room is full
        if (isGameFull()) {
            return false;
        }

        playersByTeam.computeIfAbsent(teamCode, k -> new HashSet<>()).add(playerCode);

        // Check if room is ready to start
        if (isGameFull()) {
            state = RoomState.READY;
            System.out.println("Room " + gameCode + " is ready to start!");
        }

        return true;
    }

    public synchronized void startGame() {
        if (state != RoomState.READY) {
            throw new IllegalStateException("Room is not ready to start");
        }

        state = RoomState.PLAYING;

        System.out.println("Game starting in room: " + gameCode);
    }

    private boolean isGameFull() {
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

    public RoomState getState() {
        return state;
    }

    public Quiz getQuiz() {
        return quiz;
    }
}

