package com.iskahoot.server;

import java.util.Timer;
import java.util.TimerTask;

public class ModifiedCountdownLatch {
    private final int bonusFactor;
    private final int bonusCount;
    private final long waitPeriodMillis; // Alterado para long para suportar milissegundos corretamente

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
     * Invocado por uma thread quando o jogador envia a resposta.
     * Retorna o multiplicador de pontuação.
     */
    public synchronized int countdown() {
        if (finished) {
            return 0; // Se acabou o tempo, a resposta não conta (ou conta 0)
        }

        remaining--;
        answeredCount++;

        // Verifica se está dentro dos primeiros N para receber bónus
        int multiplier = (answeredCount <= bonusCount) ? bonusFactor : 1;

        // Se todos responderam, abre a barreira
        if (remaining <= 0) {
            finish();
        }

        return multiplier;
    }

    /**
     * A thread do jogo (GameState) espera aqui.
     */
    public synchronized void await() throws InterruptedException {
        // Inicia o timer apenas quando começamos a esperar
        startTimer();

        while (!finished) {
            wait();
        }

        // Garante que o timer pára se todos responderem antes do tempo
        if (timer != null) {
            timer.cancel();
        }
    }

    private void startTimer() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (ModifiedCountdownLatch.this) {
                    if (!finished) {
                        System.out.println("Tempo esgotado (Latch)!");
                        finish();
                    }
                }
            }
        }, waitPeriodMillis);
    }

    private void finish() {
        finished = true;
        notifyAll(); // Acorda o GameState
    }
}