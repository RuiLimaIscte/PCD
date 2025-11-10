package com.iskahoot.server;

//import com.iskahoot.common.models.Player;
//import com.iskahoot.common.models.Quiz;
//import com.iskahoot.common.models.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a game room
 * Each room has its own game state and manages its own game flow
 */
//public class Room {
//    public enum RoomState {
//        WAITING,    // Waiting for players
//        READY,      // All players connected
//        PLAYING,    // Game in progress
//        FINISHED    // Game ended
//    }
//
//    private final String roomCode;
//    private final int numberOfTeams;
//    private final Quiz quiz;
//    private final Map<String, Team> teams;  // teamCode -> Team
//    private final Map<String, Player> players;  // username -> Player
//    private RoomState state;
//    private GameState gameState;
//
//    public Room(String roomCode, int numberOfTeams, Quiz quiz) {
//        this.roomCode = roomCode;
//        this.numberOfTeams = numberOfTeams;
//        this.quiz = quiz;
//        this.teams = new ConcurrentHashMap<>();
//        this.players = new ConcurrentHashMap<>();
//        this.state = RoomState.WAITING;
//    }
//
//    /**
//     * Add player to room
//     * @return true if player was added successfully
//     */
//    public synchronized boolean addPlayer(String teamCode, Player player) {
//        // Check if room is full
//        if (getConnectedPlayers() >= getExpectedPlayers()) {
//            return false;
//        }
//
//        // Get or create team
//        Team team = teams.computeIfAbsent(teamCode, Team::new);
//
//        // Check if team is full
//        if (team.isFull()) {
//            return false;
//        }
//
//        // Add player to team
//        if (!team.addPlayer(player)) {
//            return false;
//        }
//
//        players.put(player.getUsername(), player);
//
//        // Check if room is ready to start
//        if (getConnectedPlayers() == getExpectedPlayers()) {
//            state = RoomState.READY;
//            System.out.println("Room " + roomCode + " is ready to start!");
//        }
//
//        return true;
//    }
//
//    /**
//     * Start the game
//     */
//    public synchronized void startGame() {
//        if (state != RoomState.READY) {
//            throw new IllegalStateException("Room is not ready to start");
//        }
//
//        state = RoomState.PLAYING;
//        gameState = new GameState(this, quiz);
//
//        // TODO: Start game loop in separate thread
//        System.out.println("Game starting in room: " + roomCode);
//    }
//
//    public String getRoomCode() {
//        return roomCode;
//    }
//
//    public int getNumberOfTeams() {
//        return numberOfTeams;
//    }
//
//    public int getExpectedPlayers() {
//        return numberOfTeams * 2;  // 2 players per team
//    }
//
//    public int getConnectedPlayers() {
//        return players.size();
//    }
//
//    public RoomState getState() {
//        return state;
//    }
//
//    public Map<String, Team> getTeams() {
//        return teams;
//    }
//
//    public Map<String, Player> getPlayers() {
//        return players;
//    }
//
//    public GameState getGameState() {
//        return gameState;
//    }
//
//    public boolean hasTeam(String teamCode) {
//        return teams.containsKey(teamCode);
//    }
//
//    /**
//     * Print room status
//     */
//    public void printStatus() {
//        System.out.println("\n╔════════════════════════════════════════╗");
//        System.out.println("║         Room Status                    ║");
//        System.out.println("╠════════════════════════════════════════╣");
//        System.out.printf("║ Code: %-32s ║%n", roomCode);
//        System.out.printf("║ State: %-31s ║%n", state);
//        System.out.printf("║ Players: %2d/%-2d                       ║%n",
//                         getConnectedPlayers(), getExpectedPlayers());
//        System.out.printf("║ Teams: %-3d                            ║%n", teams.size());
//        System.out.println("╠════════════════════════════════════════╣");
//
//        if (!teams.isEmpty()) {
//            System.out.println("║ Teams:                                 ║");
//            for (Team team : teams.values()) {
//                System.out.printf("║   %-6s: %d/2 players (Score: %-4d)  ║%n",
//                    team.getTeamCode(),
//                    team.getPlayerCount(),
//                    team.getTeamScore()
//                );
//            }
//        }
//
//        System.out.println("╚════════════════════════════════════════╝");
//    }
//}

