package com.iskahoot.server;

public class TeamBarrier {
    private final int totalPlayers;
    private int answersReceived = 0;
    private boolean timeOut = false;

    public TeamBarrier(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    /**
     * Regista uma resposta.
     * @return true se TODOS os jogadores já responderam (sinal para acordar o jogo), false caso contrário.
     */
    public synchronized boolean registerAnswer() {
        if (timeOut) return false;

        answersReceived++;

        // Se recebemos respostas de todos os jogadores conectados
        return answersReceived >= totalPlayers;
    }

    /**
     * O GameState espera aqui até todos responderem OU o tempo acabar.
     */
    public synchronized void await(long timeoutMillis) throws InterruptedException {
        long start = System.currentTimeMillis();
        long timeRemaining = timeoutMillis;

        while (answersReceived < totalPlayers && timeRemaining > 0) {
            wait(timeRemaining);
            long now = System.currentTimeMillis();
            timeRemaining = timeoutMillis - (now - start);
        }

        if (answersReceived < totalPlayers) {
            timeOut = true;
            System.out.println("Tempo esgotado (TeamBarrier)!");
        }
    }
}