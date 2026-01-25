package com.mitchej123.hodgepodge.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncNBTParser {

    private static volatile ExecutorService executor;

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {

        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Hodgepodge-AsyncNBT-" + count.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    };

    public static void submit(Runnable task) {
        ExecutorService exec = executor;
        if (exec == null) {
            synchronized (AsyncNBTParser.class) {
                exec = executor;
                if (exec == null) {
                    exec = Executors.newSingleThreadExecutor(THREAD_FACTORY);
                    executor = exec;
                }
            }
        }
        try {
            exec.submit(task);
        } catch (RejectedExecutionException ignored) {}
    }

    public static void shutdown() {
        if (executor != null) {
            synchronized (AsyncNBTParser.class) {
                if (executor != null) {
                    executor.shutdownNow();
                    executor = null;
                }
            }
        }
    }
}
