package com.iskahoot.server;

import java.util.Timer;
import java.util.TimerTask;

public class ModifiedCountdownLatch {
    private final int bonusFactor;
    private final int bonusCount;

    private int remaining;
    private int answeredCount = 0;
    private boolean finished = false;


    public ModifiedCountdownLatch(int bonusFactor, int bonusCount, int waitPeriodMillis, int totalCount) {
        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.remaining = totalCount;
    }

    // Calcula a pontuação tendo em conta a ordem de chegada
    public synchronized int countdown() {
        if (finished) {
            return 0;
        }

        remaining--;
        answeredCount++;

        // Verifica se está dentro dos primeiros N para receber bónus
        int multiplier = (answeredCount <= bonusCount) ? bonusFactor : 1;

        // Se todos responderam, podemos marcar como terminado (opcional para a lógica de bónus, mas útil para fluxo)
        if (remaining <= 0) {
            finish();
            notifyAll();
        }

        return multiplier;
    }

    // Isto é opcional, mas pode ser útil para fluxo de controlo, no entanto para já fica comentado
//    public synchronized void await() throws InterruptedException {
//        while (!finished && remaining > 0) {
//            wait();
//        }
//    }


    public synchronized void finish() {
        if (!finished) {
            finished = true;
            notifyAll();
        }
    }

    public synchronized boolean isFinished() {
        return finished;
    }
}