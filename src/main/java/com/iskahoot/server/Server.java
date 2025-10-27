package com.iskahoot.server;

import com.iskahoot.common.models.Quiz;
import com.iskahoot.utils.QuestionLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main server class for IsKahoot game
 * Manages multiple game rooms and player connections
 */
//public class Server {
//    private static final int DEFAULT_PORT = 8080;
//
//    private final int port;
//    private ServerSocket serverSocket;
//    private final Map<String, Room> rooms;  // roomCode -> Room
//    private final Set<String> activeUsernames;  // Global username registry
//    private Quiz quiz;
//    private volatile boolean running;
//
//    public Server(int port) {
//        this.port = port;
//        this.rooms = new ConcurrentHashMap<>();
//        this.activeUsernames = ConcurrentHashMap.newKeySet();
//        this.running = false;
//    }
//
//    /**
//     * Start the server
//     */
//    public void start() throws IOException {
//        // Load questions from JSON
//        loadQuestions();
//
//        serverSocket = new ServerSocket(port);
//        running = true;
//
//        System.out.println("╔════════════════════════════════════════╗");
//        System.out.println("║     IsKahoot Server Started            ║");
//        System.out.println("╚════════════════════════════════════════╝");
//        System.out.println("Server listening on port: " + port);
//        System.out.println("Loaded quiz: " + quiz.getName());
//        System.out.println("Total questions: " + quiz.getQuestions().size());
//        System.out.println();
//
//        // Start TUI in separate thread
//        Thread tuiThread = new Thread(new ServerTUI(this));
//        tuiThread.setDaemon(true);
//        tuiThread.start();
//
//        // Accept client connections
//        acceptConnections();
//    }
//
//    /**
//     * Accept incoming client connections
//     */
//    private void acceptConnections() {
//        while (running) {
//            try {
//                Socket clientSocket = serverSocket.accept();
//                System.out.println("New client connection from: " +
//                                 clientSocket.getInetAddress().getHostAddress());
//
//                // Handle client in new thread
//                Thread clientHandler = new Thread(new PlayerHandler(this, clientSocket));
//                clientHandler.start();
//
//            } catch (IOException e) {
//                if (running) {
//                    System.err.println("Error accepting client connection: " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    /**
//     * Load questions from JSON file
//     */
//    private void loadQuestions() throws IOException {
//        try {
//            quiz = QuestionLoader.loadFromResources("questions.json");
//            System.out.println("Questions loaded successfully!");
//        } catch (IOException e) {
//            System.err.println("Failed to load questions: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    /**
//     * Create a new game room
//     */
//    public String createRoom(int numberOfTeams) {
//        String roomCode = generateRoomCode();
//        Room room = new Room(roomCode, numberOfTeams, quiz);
//        rooms.put(roomCode, room);
//        System.out.println("Created room: " + roomCode + " (Teams: " + numberOfTeams + ")");
//        return roomCode;
//    }
//
//    /**
//     * Generate unique room code
//     */
//    private String generateRoomCode() {
//        String code;
//        do {
//            code = String.format("%04d", (int)(Math.random() * 10000));
//        } while (rooms.containsKey(code));
//        return code;
//    }
//
//    /**
//     * Get room by code
//     */
//    public Room getRoom(String roomCode) {
//        return rooms.get(roomCode);
//    }
//
//    /**
//     * Register username globally
//     */
//    public boolean registerUsername(String username) {
//        return activeUsernames.add(username);
//    }
//
//    /**
//     * Unregister username
//     */
//    public void unregisterUsername(String username) {
//        activeUsernames.remove(username);
//    }
//
//    /**
//     * Get all active rooms
//     */
//    public Map<String, Room> getRooms() {
//        return rooms;
//    }
//
//    /**
//     * Stop the server
//     */
//    public void stop() {
//        running = false;
//        try {
//            if (serverSocket != null && !serverSocket.isClosed()) {
//                serverSocket.close();
//            }
//        } catch (IOException e) {
//            System.err.println("Error closing server socket: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        int port = DEFAULT_PORT;
//
//        // Parse command line arguments
//        if (args.length > 0) {
//            try {
//                port = Integer.parseInt(args[0]);
//            } catch (NumberFormatException e) {
//                System.err.println("Invalid port number. Using default: " + DEFAULT_PORT);
//            }
//        }
//
//        Server server = new Server(port);
//        try {
//            server.start();
//        } catch (IOException e) {
//            System.err.println("Failed to start server: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

