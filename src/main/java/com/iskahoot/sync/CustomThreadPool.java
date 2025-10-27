package com.iskahoot.sync;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Custom ThreadPool implementation (Optional - Phase 8)
 * 
 * Requirements:
 * - Limit concurrent games to 5
 * - Queue additional game requests
 * - Must be implemented from scratch (no Java Executors)
 */
//public class CustomThreadPool {
//    private final int maxThreads;
//    private final Queue<Runnable> taskQueue;
//    private final WorkerThread[] workers;
//    private volatile boolean shutdown;
//
//    // TODO: Phase 8 (Optional) - Implement custom thread pool
//    // - Create fixed number of worker threads
//    // - Maintain task queue
//    // - Workers pull tasks from queue
//    // - Handle shutdown gracefully
//
//    public CustomThreadPool(int maxThreads) {
//        this.maxThreads = maxThreads;
//        this.taskQueue = new LinkedList<>();
//        this.workers = new WorkerThread[maxThreads];
//        this.shutdown = false;
//
//        // TODO: Initialize and start worker threads
//    }
//
//    /**
//     * Submit a task to the pool
//     */
//    public synchronized void submit(Runnable task) {
//        // TODO: Implement
//        // - Add task to queue
//        // - Notify waiting workers
//    }
//
//    /**
//     * Shutdown the thread pool
//     */
//    public synchronized void shutdown() {
//        // TODO: Implement
//        // - Set shutdown flag
//        // - Interrupt all workers
//        // - Wait for workers to finish
//    }
//
//    /**
//     * Worker thread that executes tasks from the queue
//     */
//    private class WorkerThread extends Thread {
//        @Override
//        public void run() {
//            // TODO: Implement
//            // - Loop while not shutdown
//            // - Take task from queue (wait if empty)
//            // - Execute task
//        }
//    }
//}

