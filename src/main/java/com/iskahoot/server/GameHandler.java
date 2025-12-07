//package com.iskahoot.server;
//
//
//public class GameHandler extends Thread {
//    private GameState gameState;
//    private int contador = 0;
//
//    public GameHandler(Game game) {
//        this.gameState = new GameState(game);
//    }
//
//    public synchronized String createRoom(int numTeams, int playersPerTeam) {
//        if (this.globalQuiz == null) {
//            throw new IllegalStateException("O Quiz não foi carregado corretamente.");
//        }
//        contador++;
//        String roomCode = "game" + contador;
//
//        Game newGame = new Game(roomCode, numTeams, playersPerTeam, this.globalQuiz);
//        activeRooms.put(roomCode, newGame);
//
//        System.out.println("Sala criada: " + roomCode);
//        return roomCode;
//    }
//
//    public Game getRoom(String code) {
//        return activeRooms.get(code);
//    }
//
//
//    public void removeRoom(String code) {
//        activeRooms.remove(code);
//        System.out.println("Sala removida: " + code);
//    }
//
//}
