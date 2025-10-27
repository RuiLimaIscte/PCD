package com.iskahoot.server;

import java.util.Map;
import java.util.Scanner;

/**
 * Text User Interface for server administration
 */
//public class ServerTUI implements Runnable {
//    private final Server server;
//    private final Scanner scanner;
//
//    public ServerTUI(Server server) {
//        this.server = server;
//        this.scanner = new Scanner(System.in);
//    }
//
//    @Override
//    public void run() {
//        printMenu();
//
//        while (true) {
//            System.out.print("\n> ");
//            String input = scanner.nextLine().trim();
//
//            if (input.isEmpty()) {
//                continue;
//            }
//
//            processCommand(input);
//        }
//    }
//
//    private void printMenu() {
//        System.out.println("\n╔════════════════════════════════════════╗");
//        System.out.println("║         Server Commands                ║");
//        System.out.println("╠════════════════════════════════════════╣");
//        System.out.println("║ create <teams>  - Create new room      ║");
//        System.out.println("║ list            - List all rooms       ║");
//        System.out.println("║ status <code>   - Show room status     ║");
//        System.out.println("║ help            - Show this menu       ║");
//        System.out.println("║ exit            - Stop server          ║");
//        System.out.println("╚════════════════════════════════════════╝");
//    }
//
//    private void processCommand(String input) {
//        String[] parts = input.split("\\s+");
//        String command = parts[0].toLowerCase();
//
//        switch (command) {
//            case "create":
//                handleCreate(parts);
//                break;
//            case "list":
//                handleList();
//                break;
//            case "status":
//                handleStatus(parts);
//                break;
//            case "help":
//                printMenu();
//                break;
//            case "exit":
//                handleExit();
//                break;
//            default:
//                System.out.println("Unknown command. Type 'help' for available commands.");
//        }
//    }
//
//    private void handleCreate(String[] parts) {
//        if (parts.length < 2) {
//            System.out.println("Usage: create <number_of_teams>");
//            return;
//        }
//
//        try {
//            int teams = Integer.parseInt(parts[1]);
//            if (teams < 1 || teams > 10) {
//                System.out.println("Number of teams must be between 1 and 10");
//                return;
//            }
//
//            String roomCode = server.createRoom(teams);
//            System.out.println("✓ Room created successfully!");
//            System.out.println("  Room Code: " + roomCode);
//            System.out.println("  Teams: " + teams);
//            System.out.println("  Players needed: " + (teams * 2));
//        } catch (NumberFormatException e) {
//            System.out.println("Invalid number of teams");
//        }
//    }
//
//    private void handleList() {
//        Map<String, Room> rooms = server.getRooms();
//
//        if (rooms.isEmpty()) {
//            System.out.println("No active rooms");
//            return;
//        }
//
//        System.out.println("\n╔════════════════════════════════════════╗");
//        System.out.println("║         Active Rooms                   ║");
//        System.out.println("╠════════════════════════════════════════╣");
//
//        for (Room room : rooms.values()) {
//            System.out.printf("║ Code: %-6s | Players: %2d/%-2d | %s ║%n",
//                room.getRoomCode(),
//                room.getConnectedPlayers(),
//                room.getExpectedPlayers(),
//                room.getState()
//            );
//        }
//
//        System.out.println("╚════════════════════════════════════════╝");
//    }
//
//    private void handleStatus(String[] parts) {
//        if (parts.length < 2) {
//            System.out.println("Usage: status <room_code>");
//            return;
//        }
//
//        String roomCode = parts[1];
//        Room room = server.getRoom(roomCode);
//
//        if (room == null) {
//            System.out.println("Room not found: " + roomCode);
//            return;
//        }
//
//        room.printStatus();
//    }
//
//    private void handleExit() {
//        System.out.println("Shutting down server...");
//        server.stop();
//        System.exit(0);
//    }
//}

