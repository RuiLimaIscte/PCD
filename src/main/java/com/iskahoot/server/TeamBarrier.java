package com.iskahoot.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Barreira de coordenação para perguntas de equipa.
 * Armazena as respostas e só liberta/calcula quando todos os membros da equipa responderem.
 */
public class TeamBarrier {
    private final int teamSize;
    private final Map<String, Integer> teamAnswers; // playerCode -> answerIndex
    private boolean finished = false;

    public TeamBarrier(int teamSize) {
        this.teamSize = teamSize;
        this.teamAnswers = new HashMap<>();
    }

    /**
     * Regista a resposta de um membro da equipa.
     * Usa notifyAll() quando a equipa preenche a barreira.
     */
    public synchronized void registerAnswer(String playerCode, int answerIndex) {
        if (finished) return;

        teamAnswers.put(playerCode, answerIndex);

        // Se todos os membros da equipa responderam
        if (teamAnswers.size() >= teamSize) {
            finished = true;
            notifyAll(); // Acorda quem estiver à espera (se houver lógica de espera específica por equipa)
        }
    }

    // --- MUDANÇA 2: Método bloqueante ---
    // A thread da equipa vai ficar presa aqui até registerAnswer chamar notifyAll
    public synchronized void await() throws InterruptedException {
        while (!finished) {
            wait(); // Liberta o lock e dorme
        }
    }

    /**
     * Calcula a pontuação da equipa baseada nas regras de consenso.
     * Regra 1: Se TODOS acertarem -> Pontuação Duplicada.
     * Regra 2: Se ALGUÉM falhar -> Melhor pontuação individual (sem bónus).
     */
    public synchronized int calculateTeamScore(int correctIndex, int questionPoints) {
        if (teamAnswers.isEmpty()) return 0;

        boolean allCorrect = true;
        boolean anyCorrect = false;

        for (Integer answer : teamAnswers.values()) {
            if (answer == correctIndex) {
                anyCorrect = true;
            } else {
                allCorrect = false;
            }
        }
        //Acertaram todos
        if (allCorrect) {
            return (questionPoints * teamSize) * 2; // Bónus de equipa perfeita
        } else if (anyCorrect) {
            return questionPoints; // Melhor pontuação (alguém acertou)
        } else {
            return 0; // Ninguém acertou
        }
    }

    public synchronized int getAnswerCount() {
        return teamAnswers.size();
    }

    // Método para forçar o fim se o tempo acabar (para desbloquear o wait)
    public synchronized void forceFinish() {
        finished = true;
        notifyAll();
    }
}