package com.iskahoot.server;

public class ModifiedCountdownLatch {
    private final int bonusFactor;
    private final int bonusCount;

    private int remaining;
    private int answeredCount = 0;
    private boolean finished = false;


    public ModifiedCountdownLatch(int bonusFactor, int bonusCount, int totalCount) {
        this.bonusFactor = bonusFactor;
        this.bonusCount = bonusCount;
        this.remaining = totalCount;
    }

    // Calcula o bonus tendo em conta a ordem de chegada
    public synchronized int countdown() {
        if (finished) {
            return 0;
        }

        remaining--;
        answeredCount++;

        //verificar se recebe bonus
        int multiplier = (answeredCount <= bonusCount) ? bonusFactor : 1;

        // Se todos responderam
        if (remaining <= 0) {
            finish();
        }
        return multiplier;
    }

    // logica de wait(roundtime) no gamestate
//    public synchronized void await() throws InterruptedException {
//        while (!finished && remaining > 0) {
//            wait(roundtime);
//        }
//    }

    public synchronized void finish() {
        if (!finished) {
            finished = true;
            notifyAll();
        }
    }

}