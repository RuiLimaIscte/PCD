package com.iskahoot.server;

import java.util.Timer;
import java.util.TimerTask;

public class ModifiedCountdownLatch {
    private final int bonusFactor;
    private final int bonusCount;
    private final long waitPeriodMillis;

    private int remaining;
    private int answeredCount = 0;
    private boolean finished = false;
    private Timer timer;

    public ModifiedCountdownLatch(int bonusFactor, int bonusCount, int waitPeriodMillis, int totalCount) {
        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.waitPeriodMillis = waitPeriodMillis;
        this.remaining = totalCount;
    }

    /**
     * Invocado quando o jogador envia a resposta.
     * Retorna o multiplicador de pontuação (ex: 2 para os primeiros, 1 para os restantes).
     */

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
        }
        return multiplier;
    }

    /**
     * A thread do jogo pode esperar aqui (opcional, pois o GameState usa o seu próprio wait)
     */
    public synchronized void await() throws InterruptedException {
        startTimer();
        while (!finished && remaining > 0) {
            wait();
        }
        if (timer != null) timer.cancel();
    }

    private void startTimer() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (ModifiedCountdownLatch.this) {
                    finish();
                }
            }
        }, waitPeriodMillis);
    }

    private synchronized void finish() {
        finished = true;
        notifyAll();
    }
}