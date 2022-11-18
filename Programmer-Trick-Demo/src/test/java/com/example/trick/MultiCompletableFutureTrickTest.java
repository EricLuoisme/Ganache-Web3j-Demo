package com.example.trick;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Roylic
 * 2022/11/18
 */
public class MultiCompletableFutureTrickTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);


    @Test
    public void dispatchAndCombineCompletableFutureTrickTest() {

        List<String> theList = new LinkedList<>();
        theList.add("a");
        theList.add("b");
        theList.add("c");
        theList.add("d");
        theList.add("e");
        theList.add("f");

        // storing all completable future, void for only executing
        List<CompletableFuture<Void>> futureList = new LinkedList<>();

        // dispatch
        theList.forEach(single -> {
            // 1. submit to the thread pool
            CompletableFuture<Void> singleFuture = CompletableFuture.runAsync(() -> justClick(single), executorService);
            // 2. store it into the CompletableFuture list
            futureList.add(singleFuture);
        });

        // combine
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        voidCompletableFuture.join();
    }


    private void justClick(String click) {
        try {
            // simulate doing network request
            TimeUnit.MILLISECONDS.sleep(100 + new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(click);
    }


}
