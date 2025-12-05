package com.iskahoot.server;


import com.iskahoot.common.models.Quiz;
import static com.iskahoot.utils.QuestionLoader.loadFromFile; // O teu loader utilitário

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private final Map<String, Room> activeRooms = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private int contador = 0;

    private Quiz globalQuiz;

    private static RoomManager instance;

    private RoomManager() {
        try {
            // Carrega o quiz do ficheiro UMA VEZ no arranque
            // Ajusta o caminho conforme a tua estrutura de projeto
            this.globalQuiz = loadFromFile("src/main/resources/questions.json");

            System.out.println("Quiz global carregado com sucesso: " + globalQuiz.getQuestions().size() + " perguntas.");
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o quiz!");
            e.printStackTrace();

            //TODO LIDAR MELHOR COM ISTO (// QUEBRAR O SERVIDOR SE NÃO CARREGAR O QUIZ)
        }
    }

    public static synchronized RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    public synchronized String createRoom(int numTeams, int playersPerTeam) {
        if (this.globalQuiz == null) {
            throw new IllegalStateException("O Quiz não foi carregado corretamente.");
        }
        contador++;
        String roomCode = "game" + contador;

        Room newRoom = new Room(roomCode, numTeams, playersPerTeam, this.globalQuiz);
        activeRooms.put(roomCode, newRoom);

        System.out.println("Sala criada: " + roomCode);
        return roomCode;
    }

    public Room getRoom(String code) {
        return activeRooms.get(code);
    }


    public void removeRoom(String code) {
        activeRooms.remove(code);
        System.out.println("Sala removida: " + code);
    }
}