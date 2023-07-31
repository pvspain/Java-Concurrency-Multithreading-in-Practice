package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

public class Lesson1C {
    // Joining the Results of the Tasks
    // PickFruitsWithRecursiveAction

    public static void main(String[] args) {
        AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
        ForkJoinPool pool = ForkJoinPool.commonPool();

        System.out.println("CPU Core: " + Runtime.getRuntime().availableProcessors());
        System.out.println("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        System.out.println("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        
        long startTime = System.currentTimeMillis();
        PickFruitAction task = new PickFruitAction(appleTrees, 0, appleTrees.length - 1);
        pool.invoke(task);

        long endTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("Done in " + (endTime - startTime));
    }

    public static class PickFruitAction extends RecursiveAction {

        private final AppleTree[] appleTrees;
        private final int startInclusive;
        private final int endInclusive;

        private final int taskThreshold = 4; 

        public PickFruitAction(AppleTree[] array, int startInclusive, int endInclusive) {
            this.appleTrees = array;
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
        }

        @Override
        protected void compute() {

            if (endInclusive - startInclusive < taskThreshold) {
                doCompute();
            }

            int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

            PickFruitAction leftSum = new PickFruitAction(appleTrees, startInclusive, midpoint);
            PickFruitAction rightSum = new PickFruitAction(appleTrees, midpoint + 1, endInclusive);

            // Asynchronous fork()
            rightSum.fork();
            // Execute synchronous compute() then wait on async task with join()
            leftSum.compute();
            rightSum.join();

        }

        protected int doCompute() {
            return IntStream.rangeClosed(startInclusive, endInclusive) //
                    .map(i -> appleTrees[i].pickApples()) //
                    .sum();
        }

    }
}
