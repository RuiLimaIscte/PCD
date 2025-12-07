//package com.iskahoot.server;
//
//public class RoundTimer extends Thread {
//    private final int timeLimitSeconds;
//    private final Runnable onTimeExpire; // Ação a executar se o tempo acabar
//
//    public RoundTimer(int timeLimitSeconds, Runnable onTimeExpire) {
//        this.timeLimitSeconds = timeLimitSeconds;
//        this.onTimeExpire = onTimeExpire;
//    }
//
//    @Override
//    public void run() {
//        try {
//            // 1. Tenta dormir pelo tempo estipulado
//            // Slide "threads-handout", pág 26: Thread.sleep(time)
//            // Multiplicamos por 1000 porque o argumento é em milissegundos
//            Thread.sleep(timeLimitSeconds * 1000L);
//
//            // 2. Se chegar aqui, ninguém interrompeu -> O TEMPO ACABOU
//            System.out.println("[Timer] O tempo da ronda esgotou-se (" + timeLimitSeconds + "s).");
//
//            // Executa a lógica de "fim de tempo" (ex: fechar ronda)
//            if (onTimeExpire != null) {
//                onTimeExpire.run();
//            }
//
//        } catch (InterruptedException e) {
//            // 3. Se cair aqui, foi interrompido -> TODOS RESPONDERAM ANTES DO TEMPO
//            // Slide "threads-handout", pág 27/28: Interrupção de threads
//            System.out.println("[Timer] O temporizador foi cancelado (todos responderam).");
//        }
//    }
//}