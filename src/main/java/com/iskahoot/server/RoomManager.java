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

//    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//    private static final int CODE_LENGTH = 5;

    // VARIÁVEL PARA O QUIZ ÚNICO
    private Quiz globalQuiz;

    private static RoomManager instance;

    // O construtor é privado, ideal para carregar o Quiz uma única vez
    private RoomManager() {
        try {
            // Carrega o quiz do ficheiro UMA VEZ no arranque
            // Ajusta o caminho conforme a tua estrutura de projeto
            this.globalQuiz = new Quiz(
                    loadFromFile("src/main/resources/questions.json").getName(),
                    loadFromFile("src/main/resources/questions.json").getQuestions()
            );
            System.out.println("Quiz global carregado com sucesso: " + globalQuiz.getQuestions().size() + " perguntas.");
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o quiz!");
            e.printStackTrace();
            // Em produção, talvez quisesses fechar o servidor aqui
        }
    }

    public static synchronized RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    public synchronized String  createRoom(int numTeams, int playersPerTeam) {
        if (this.globalQuiz == null) {
            throw new IllegalStateException("O Quiz não foi carregado corretamente.");
        }
        contador++;
        String roomCode= "game"+contador;

            // Passamos sempre o this.globalQuiz
            Room newRoom = new Room(roomCode, numTeams, playersPerTeam, this.globalQuiz);
            activeRooms.put(roomCode, newRoom);

        System.out.println("Sala criada: " + roomCode);
        return  roomCode;
    }

    /**
     * Recupera uma sala pelo código.
     */
    public Room getRoom(String code) {
        return activeRooms.get(code);
    }
    public String getRoomCode(Room room) {
        for (Map.Entry<String, Room> entry : activeRooms.entrySet()) {
            if (entry.getValue().equals(room)) {
                return entry.getKey();
            }
        }
        return null; // Sala não encontrada
    }

    /**
     * Remove uma sala (ex: quando o jogo termina).
     */
    public void removeRoom(String code) {
        activeRooms.remove(code);
        System.out.println("Sala removida: " + code);
    }

    // Gera um código alfanumérico aleatório
//    private String generateRandomCode() {
//        StringBuilder sb = new StringBuilder(CODE_LENGTH);
//        for (int i = 0; i < CODE_LENGTH; i++) {
//            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
//        }
//        return sb.toString();
//    }
}