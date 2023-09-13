package com.example.trick.forkjoinpool;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinTest {

    // forkJoinPool stuff
    public static void main(String[] args) {
        // init a task
        int[] ints = {1, 4, 3, 123, 5435, 234, 6, 4365, 12, -5};
        CustomRecursiveTask customRecursiveTask = new CustomRecursiveTask(ints);
        // calculate
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.execute(customRecursiveTask);
        Integer compute = customRecursiveTask.compute();
        System.out.println(compute);
    }
}
