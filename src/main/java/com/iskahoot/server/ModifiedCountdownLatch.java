package com.iskahoot.server;

public class ModifiedCountdownLatch {
    private final int bonusFactor;
    private final int bonusCount;
    private final int waitPeriod;

    private int remaining;
    private int answered = 0;
    private boolean finished = false;


    public ModifiedCountdownLatch(int bonusFactor, int bonusCount, int waitPeriod, int count) {

        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.waitPeriod = waitPeriod;
        this.remaining = count;


    }
//
//    public synchronized int countdown() {
//        return 0;
//    }
//
//    public synchronized void await() throws InterruptedException {
//
//    }
    /**
     * Invocado por uma thread quando o jogador envia a resposta.
     * Retorna:
     *  - bonusFactor para os primeiros bonusCount respondentes,
     *  - 1 para os restantes (se ainda dentro do período),
     *  - 0 se a ronda já tiver terminado (timeout ou todos responderam).
     *
     * Regras de sincronização: threads podem chamar countdown() concorrentemente.
     */
    public synchronized int countdown() {
        // Se já terminou, rejeita (resposta fora do prazo)
        if (finished) {
            return 0;
        }

        // contabiliza a resposta na ordem de chegada
        answered++;
        int multiplier = (answered <= bonusCount) ? bonusFactor : 1;

        // decrementa restantes
        remaining--;

        // se chegamos a 0 restantes, libertamos todos (termina antes do timeout)
        if (remaining <= 0) {
            releaseAll();
        }

        return multiplier;
    }

    /**
     * Aguarda até que a latch seja libertada (todos responderam ou timeout).
     * Inicia o temporizador se ainda não tiver sido iniciado.
     */
//    public synchronized void await() throws InterruptedException {
//        startTimerIfNeeded();
//
//        while (!finished) {
//            wait();
//        }
//    }

    // inicia o timer do waitPeriod (apenas uma vez). O timer faz releaseAll() quando expira.
//    private synchronized void startTimerIfNeeded() {
//        if (timerStarted) return;
//        timerStarted = true;
//
//        Thread timer = new Thread(() -> {
//            try {
//                Thread.sleep(waitPeriodMillis);
//            } catch (InterruptedException ignored) {
//                // se for interrompido, saímos sem forçar release (em geral não vamos interromper o timer)
//            }
//
//            synchronized (ModifiedCountdownLatch.this) {
//                // se ainda não terminou, termina por timeout
//                if (!finished) {
//                    releaseAll();
//                }
//            }
//        });
//
//        timer.setDaemon(true);
//        timer.start();
//    }

    // liberta a latch e notifica todas as threads em await()
    private synchronized void releaseAll() {
        if (!finished) {
            finished = true;
            notifyAll();
        }
    }
}