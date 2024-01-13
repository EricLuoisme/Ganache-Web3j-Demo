package com.example.trick.ex;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

public class ThreadPoolExceptionTest {

    private static final ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            2, 4, 500, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100),
            new BasicThreadFactory.Builder()
                    .build(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws InterruptedException {
        poolExecutor.execute(new TaskLogWrapper(new OperationTask()));
        TimeUnit.SECONDS.sleep(10);
        System.out.println("Main Thread Stop");
    }

    // Method 1, we could use a wrapper, for task logging
    public static class TaskLogWrapper implements Runnable {
        private final Runnable runnable;

        public TaskLogWrapper(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                System.out.println("Log wrapper got exception: " + e.getMessage());
            }
        }
    }


    public static class OperationTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Begin the task");
            concreteTask();
            System.out.println("End the task");
        }
    }

    public static void concreteTask() {
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += i;
        }
        if (sum > 10) {
            throw new RuntimeException("Run time exception, surprise!");
        }
        System.out.println("Finished calculation got: " + sum);
    }
}
