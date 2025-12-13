package com.iskahoot.server;

import java.util.HashMap;
import java.util.Map;


public class TeamBarrier {
    private final int teamSize;
    private final Map<String, Integer> teamAnswers; //pode ser uma lista
    private boolean finished = false;

    public TeamBarrier(int teamSize) {
        this.teamSize = teamSize;
        this.teamAnswers = new HashMap<>();
    }

    public synchronized void registerAnswer(String playerCode, int answerIndex) {
        if (finished) return;

        teamAnswers.put(playerCode, answerIndex);

        if (teamAnswers.size() >= teamSize) {
            finished = true;
            notifyAll();
        }
    }

    //presa até registerAnswer chamar notifyAll
    public synchronized void await() throws InterruptedException {
        while (!finished) {
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
        if (allCorrect) {
            return (questionPoints * teamSize) * 2;
        } else if (anyCorrect) {
            return questionPoints;
        } else {
            return 0;
        }
    }

    // forçar o fim se o tempo acabar
    public synchronized void forceFinish() {
        finished = true;
        notifyAll();
    }
}