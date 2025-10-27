package com.iskahoot.sync;

/**
 * Custom Barrier implementation for team questions
 * 
 * Requirements (Phase 6):
 * - Coordinates team responses
 * - Waits for all team members OR timeout
 * - barrierAction: Calculate team score when barrier is reached
 *   - All correct → 2x points
 *   - Any wrong → best individual score only (no bonus)
 * - Must be implemented from scratch (no Java standard library synchronization)
 */
//public class ModifiedBarrier {
//    private final int parties;  // Number of threads to wait for
//    private int count;  // Current count of waiting threads
//    private final long timeoutMillis;
//    private final Runnable barrierAction;
//    private boolean broken;
//
//    // TODO: Phase 6 - Implement custom barrier
//    // - Use wait() and notifyAll() for synchronization
//    // - Implement timeout mechanism
//    // - Execute barrierAction when all parties arrive or timeout
//
//    public ModifiedBarrier(int parties, long timeoutMillis, Runnable barrierAction) {
//        this.parties = parties;
//        this.count = parties;
//        this.timeoutMillis = timeoutMillis;
//        this.barrierAction = barrierAction;
//        this.broken = false;
//    }
//
//    /**
//     * Wait at the barrier
//     * @return true if barrier was reached normally, false if timeout or broken
//     */
//    public synchronized boolean await() {
//        // TODO: Implement
//        // - Decrement count
//        // - If count == 0, execute barrierAction and notify all
//        // - Otherwise, wait with timeout
//        // - Handle timeout case
//        return false;
//    }
//
//    /**
//     * Reset the barrier for next round
//     */
//    public synchronized void reset() {
//        this.count = parties;
//        this.broken = false;
//    }
//
//    /**
//     * Break the barrier (e.g., on timeout)
//     */
//    public synchronized void breakBarrier() {
//        this.broken = true;
//        notifyAll();
//    }
//
//    public boolean isBroken() {
//        return broken;
//    }
//}

