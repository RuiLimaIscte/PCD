package com.iskahoot.client;

//import com.iskahoot.common.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client application for IsKahoot game
 * Connects to server and handles game interaction
 */
//public class Client {
//    private final String serverHost;
//    private final int serverPort;
//    private final String roomCode;
//    private final String teamCode;
//    private final String username;
//
//    private Socket socket;
//    private ObjectOutputStream out;
//    private ObjectInputStream in;
//    private ClientGUI gui;
//    private volatile boolean connected;
//
//    public Client(String serverHost, int serverPort, String roomCode,
//                 String teamCode, String username) {
//        this.serverHost = serverHost;
//        this.serverPort = serverPort;
//        this.roomCode = roomCode;
//        this.teamCode = teamCode;
//        this.username = username;
//        this.connected = false;
//    }
//
//    /**
//     * Connect to server and join room
//     */
//    public boolean connect() {
//        try {
//            // Connect to server
//            socket = new Socket(serverHost, serverPort);
//            out = new ObjectOutputStream(socket.getOutputStream());
//            out.flush();
//            in = new ObjectInputStream(socket.getInputStream());
//
//            System.out.println("Connected to server: " + serverHost + ":" + serverPort);
//
//            // Send join request
//            JoinRequest joinRequest = new JoinRequest(roomCode, teamCode, username);
//            Message joinMessage = new Message(Message.MessageType.JOIN_ROOM, joinRequest);
//            out.writeObject(joinMessage);
//            out.flush();
//
//            // Wait for response
//            Message response = (Message) in.readObject();
//
//            if (response.getType() == Message.MessageType.JOIN_ACCEPTED) {
//                connected = true;
//                System.out.println("Joined room successfully!");
//                System.out.println("Message: " + response.getPayload());
//
//                // Start message listener thread
//                Thread listenerThread = new Thread(this::listenForMessages);
//                listenerThread.setDaemon(true);
//                listenerThread.start();
//
//                return true;
//            } else {
//                System.err.println("Join rejected: " + response.getPayload());
//                return false;
//            }
//
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("Connection failed: " + e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * Listen for messages from server
//     */
//    private void listenForMessages() {
//        try {
//            while (connected) {
//                Message message = (Message) in.readObject();
//                handleMessage(message);
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            if (connected) {
//                System.err.println("Connection lost: " + e.getMessage());
//                connected = false;
//            }
//        }
//    }
//
//    /**
//     * Handle incoming message from server
//     */
//    private void handleMessage(Message message) {
//        switch (message.getType()) {
//            case GAME_STARTING:
//                System.out.println("Game is starting!");
//                // TODO: Update GUI
//                break;
//
//            case QUESTION_BROADCAST:
//                System.out.println("New question received");
//                // TODO: Display question in GUI
//                break;
//
//            case SCOREBOARD_UPDATE:
//                System.out.println("Scoreboard updated");
//                // TODO: Update scoreboard in GUI
//                break;
//
//            case ROUND_ENDED:
//                System.out.println("Round ended");
//                // TODO: Show round results
//                break;
//
//            case GAME_ENDED:
//                System.out.println("Game ended!");
//                // TODO: Show final results
//                break;
//
//            default:
//                System.out.println("Received: " + message.getType());
//        }
//    }
//
//    /**
//     * Submit answer to server
//     */
//    public void submitAnswer(int answerIndex) {
//        try {
//            AnswerSubmission answer = new AnswerSubmission(username, answerIndex);
//            Message message = new Message(Message.MessageType.SUBMIT_ANSWER, answer);
//            out.writeObject(message);
//            out.flush();
//            System.out.println("Answer submitted: " + answerIndex);
//        } catch (IOException e) {
//            System.err.println("Failed to submit answer: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Disconnect from server
//     */
//    public void disconnect() {
//        connected = false;
//        try {
//            if (out != null) {
//                Message disconnectMsg = new Message(Message.MessageType.DISCONNECT, null);
//                out.writeObject(disconnectMsg);
//                out.flush();
//            }
//
//            if (in != null) in.close();
//            if (out != null) out.close();
//            if (socket != null) socket.close();
//
//            System.out.println("Disconnected from server");
//        } catch (IOException e) {
//            System.err.println("Error during disconnect: " + e.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        // Parse command line arguments
//        // java clienteKahoot {IP PORT Sala Equipa Username}
//
//        if (args.length < 5) {
//            System.err.println("Usage: java Client <IP> <PORT> <RoomCode> <TeamCode> <Username>");
//            System.err.println("Example: java Client localhost 8080 1234 TEAM1 Alice");
//            System.exit(1);
//        }
//
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
//        String roomCode = args[2];
//        String teamCode = args[3];
//        String username = args[4];
//
//        Client client = new Client(host, port, roomCode, teamCode, username);
//
//        if (client.connect()) {
//            System.out.println("Connected successfully! Waiting for game to start...");
//
//            // TODO: Launch GUI
//            // For now, keep the client running
//            try {
//                Thread.sleep(Long.MAX_VALUE);
//            } catch (InterruptedException e) {
//                client.disconnect();
//            }
//        } else {
//            System.err.println("Failed to connect to server");
//            System.exit(1);
//        }
//    }
//}

