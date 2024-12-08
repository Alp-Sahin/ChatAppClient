package org.example.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadFactory {
    private static ThreadFactory threadFactory;
    ExecutorService threadPool;
    private ThreadFactory() {
        threadPool = Executors.newFixedThreadPool(20);
    }
    public static ThreadFactory getInstance() {
        if (threadFactory == null) {
            threadFactory = new ThreadFactory();
        }
        return threadFactory;
    }
    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
