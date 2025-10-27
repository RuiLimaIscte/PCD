package com.iskahoot.sync;

/**
 * Custom Semaphore implementation for individual questions
 * 
 * Requirements (Phase 6):
 * - waitForTimeout(): Blocks until all licenses consumed OR timeout expires
 * - acquire(): Returns integer score value (bonus for first 2 responders)
 * - First 2 correct answers get 2x points
 * - Must be implemented from scratch (no Java standard library synchronization)
 * 
 * Suggested approach: Cyclic Barrier + Timer
 */
//public class ModifiedSemaphore {
//    private int licenses;
//    private final int initialLicenses;
//    private final long timeoutMillis;
//    private int bonusCount;  // Track how many bonus slots are left
//    private static final int MAX_BONUS = 2;
//
//    // TODO: Phase 6 - Implement custom semaphore
//    // - Use wait() and notifyAll() for synchronization
//    // - Track submission order for bonus scoring
//    // - Implement timeout mechanism
//
//    public ModifiedSemaphore(int licenses, long timeoutMillis) {
//        this.licenses = licenses;
//        this.initialLicenses = licenses;
//        this.timeoutMillis = timeoutMillis;
//        this.bonusCount = MAX_BONUS;
//    }
//
//    /**
//     * Acquire a license and return the score multiplier
//     * @param basePoints Base points for the question
//     * @return Actual points to award (with bonus if applicable)
//     */
//    public synchronized int acquire(int basePoints) {
//        // TODO: Implement
//        // - Decrement licenses
//        // - Check if this is one of first 2 (bonus)
//        // - Return appropriate score
//        // - Notify waiting threads
//        return basePoints;
//    }
//
//    /**
//     * Wait for all licenses to be consumed or timeout
//     * @return true if all licenses consumed, false if timeout
//     */
//    public synchronized boolean waitForTimeout() {
//        // TODO: Implement
//        // - Wait until licenses == 0 OR timeout
//        // - Use wait(timeout) mechanism
//        return false;
//    }
//
//    /**
//     * Reset for next round
//     */
//    public synchronized void reset() {
//        this.licenses = initialLicenses;
//        this.bonusCount = MAX_BONUS;
//    }
//}

