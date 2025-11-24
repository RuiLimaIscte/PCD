package com.iskahoot.server;

import com.iskahoot.common.models.Player;
import com.iskahoot.common.models.Quiz;
import com.iskahoot.common.models.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Room {
    public enum RoomState {
        WAITING,    // Waiting for players
        READY,      // All players connected
        PLAYING,    // Game in progress
        FINISHED    // Game ended
    }

    private final String roomCode;
    private final int numberOfTeams;
    private final Quiz quiz;
    private final Map<String, Team> teams;  // teamCode -> Team
    private final Map<String, Player> players;  // username -> Player
    private RoomState state;
    private GameState gameState;

    public Room(String roomCode, int numberOfTeams, Quiz quiz) {
        this.roomCode = roomCode;
        this.numberOfTeams = numberOfTeams;
        this.quiz = quiz;
        this.teams = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        this.state = RoomState.WAITING;
    }

    public synchronized boolean addPlayer(String teamCode, Player player) {
        // Check if room is full
        if (getConnectedPlayers() >= getExpectedPlayers()) {
            return false;
        }

        // Get or create team
        Team team = teams.computeIfAbsent(teamCode, Team::new);

        // Check if team is full
        if (team.isFull()) {
            return false;
        }

        // Add player to team
        if (!team.addPlayer(player)) {
            return false;
        }

        players.put(player.getUsername(), player);

        // Check if room is ready to start
        if (getConnectedPlayers() == getExpectedPlayers()) {
            state = RoomState.READY;
            System.out.println("Room " + roomCode + " is ready to start!");
        }

        return true;
    }

    public synchronized void startGame() {
        if (state != RoomState.READY) {
            throw new IllegalStateException("Room is not ready to start");
        }

        state = RoomState.PLAYING;
        gameState = new GameState(this);

        System.out.println("Game starting in room: " + roomCode);
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getExpectedPlayers() {
        return numberOfTeams * 2;  // 2 players per team
    }

    public int getConnectedPlayers() {
        return players.size();
    }

    public RoomState getState() {
        return state;
    }

    public Map<String, Team> getTeams() {
        return teams;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean hasTeam(String teamCode) {
        return teams.containsKey(teamCode);
    }

    public Quiz getQuiz() {
        return quiz;
    }
}

