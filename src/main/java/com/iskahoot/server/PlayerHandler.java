package com.iskahoot.server;

import com.iskahoot.common.messages.*;
import com.iskahoot.common.models.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles communication with a single player
 * Each player connection runs in its own thread
 */
//public class PlayerHandler implements Runnable {
//    private final Server server;
//    private final Socket socket;
//    private ObjectOutputStream out;
//    private ObjectInputStream in;
//    private Player player;
//    private Room room;
//
//    public PlayerHandler(Server server, Socket socket) {
//        this.server = server;
//        this.socket = socket;
//    }
//
//    @Override
//    public void run() {
//        try {
//            // Setup streams
//            out = new ObjectOutputStream(socket.getOutputStream());
//            out.flush();
//            in = new ObjectInputStream(socket.getInputStream());
//
//            // Handle join request
//            if (!handleJoin()) {
//                return;  // Join failed, close connection
//            }
//
//            // Main message loop
//            handleMessages();
//
//        } catch (IOException e) {
//            System.err.println("Player handler error: " + e.getMessage());
//        } finally {
//            cleanup();
//        }
//    }
//
//    /**
//     * Handle initial join request
//     */
//    private boolean handleJoin() throws IOException {
//        try {
//            // Read join request
//            Message joinMessage = (Message) in.readObject();
//
//            if (joinMessage.getType() != Message.MessageType.JOIN_ROOM) {
//                sendMessage(new Message(Message.MessageType.JOIN_REJECTED,
//                    "Expected JOIN_ROOM message"));
//                return false;
//            }
//
//            JoinRequest request = (JoinRequest) joinMessage.getPayload();
//
//            // Validate username (must be unique globally)
//            if (!server.registerUsername(request.getUsername())) {
//                sendMessage(new Message(Message.MessageType.JOIN_REJECTED,
//                    "Username already in use"));
//                return false;
//            }
//
//            // Validate room
//            room = server.getRoom(request.getRoomCode());
//            if (room == null) {
//                server.unregisterUsername(request.getUsername());
//                sendMessage(new Message(Message.MessageType.JOIN_REJECTED,
//                    "Room not found"));
//                return false;
//            }
//
//            // Create player and add to room
//            player = new Player(request.getUsername(), request.getTeamCode());
//            if (!room.addPlayer(request.getTeamCode(), player)) {
//                server.unregisterUsername(request.getUsername());
//                sendMessage(new Message(Message.MessageType.JOIN_REJECTED,
//                    "Team is full or room is full"));
//                return false;
//            }
//
//            // Send acceptance
//            sendMessage(new Message(Message.MessageType.JOIN_ACCEPTED,
//                "Welcome to room " + request.getRoomCode()));
//
//            System.out.println("Player joined: " + request.getUsername() +
//                             " (Room: " + request.getRoomCode() +
//                             ", Team: " + request.getTeamCode() + ")");
//
//            // Check if room is ready to start
//            if (room.getState() == Room.RoomState.READY) {
//                // TODO: Start game
//            }
//
//            return true;
//
//        } catch (ClassNotFoundException e) {
//            System.err.println("Invalid message format");
//            return false;
//        }
//    }
//
//    /**
//     * Handle incoming messages from player
//     */
//    private void handleMessages() throws IOException {
//        try {
//            while (true) {
//                Message message = (Message) in.readObject();
//
//                switch (message.getType()) {
//                    case SUBMIT_ANSWER:
//                        handleAnswer(message);
//                        break;
//                    case DISCONNECT:
//                        return;
//                    default:
//                        System.out.println("Unknown message type: " + message.getType());
//                }
//            }
//        } catch (ClassNotFoundException e) {
//            System.err.println("Invalid message format");
//        }
//    }
//
//    /**
//     * Handle answer submission
//     */
//    private void handleAnswer(Message message) {
//        AnswerSubmission answer = (AnswerSubmission) message.getPayload();
//        System.out.println("Answer received from " + player.getUsername() +
//                         ": " + answer.getAnswerIndex());
//
//        // TODO: Process answer through semaphore/barrier
//    }
//
//    /**
//     * Send message to player
//     */
//    public void sendMessage(Message message) throws IOException {
//        out.writeObject(message);
//        out.flush();
//    }
//
//    /**
//     * Cleanup resources
//     */
//    private void cleanup() {
//        try {
//            if (player != null) {
//                server.unregisterUsername(player.getUsername());
//                System.out.println("Player disconnected: " + player.getUsername());
//            }
//
//            if (in != null) in.close();
//            if (out != null) out.close();
//            if (socket != null) socket.close();
//        } catch (IOException e) {
//            System.err.println("Error during cleanup: " + e.getMessage());
//        }
//    }
//}

