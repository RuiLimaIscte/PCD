package com.iskahoot.server;

import java.util.HashMap;
import java.util.Map;


public class TeamBarrier {
    private final int teamSize;
    private final Map<String, Integer> teamAnswers; // playerCode -> answerIndex
    private boolean finished = false;

    public TeamBarrier(int teamSize) {
        this.teamSize = teamSize;
        this.teamAnswers = new HashMap<>();
    }


    public synchronized void registerAnswer(String playerCode, int answerIndex) {
        if (finished) return;

        teamAnswers.put(playerCode, answerIndex);

        // Se todos os membros da equipa responderam
        if (teamAnswers.size() >= teamSize) {
            finished = true;
            // Acorda quem estiver à espera
            notifyAll();
        }
    }

    // A thread da equipa fica presa até registerAnswer chamar notifyAll
    public synchronized void await() throws InterruptedException {
        while (!finished) {
            // Liberta o lock e dorme
            wait();
        }
    }

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
            // Bónus de equipa
            return (questionPoints * teamSize) * 2;
        } else if (anyCorrect) {
           // Melhor pontuação individual
            return questionPoints;
        } else {
            // Ninguém acertou
            return 0;
        }
    }

    // forçar o fim se o tempo acabar
    public synchronized void forceFinish() {
        finished = true;
        notifyAll();
    }

    public synchronized int getAnswerCount() {
        return teamAnswers.size();
    }


}