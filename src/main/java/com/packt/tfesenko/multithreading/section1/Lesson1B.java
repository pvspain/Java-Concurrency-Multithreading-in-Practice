package com.packt.tfesenko.multithreading.section1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

public class Lesson1B {
    // Joining the REsults of the Tasks
    // PickFruitsWithRecursiveTask

    public static void main(String[] args) {
        AppleTree[] appleTrees = AppleTree.newTreeGarden(12);
        ForkJoinPool pool = ForkJoinPool.commonPool();

        PickFruitTask task = new PickFruitTask(appleTrees, 0, appleTrees.length - 1);
        int result = pool.invoke(task);

        System.out.println();
        System.out.println("Total apples picked: " + result);
    }

    public static class PickFruitTask extends RecursiveTask<Integer> {

        private final AppleTree[] appleTrees;
        private final int startInclusive;
        private final int endInclusive;

        private final int taskThreshold = 4;

        public PickFruitTask(AppleTree[] array, int startInclusive, int endInclusive) {
            this.appleTrees = array;
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
        }

        @Override
        protected Integer compute() {

            if (endInclusive - startInclusive < taskThreshold) {
                return doCompute();
            }

            int midpoint = startInclusive + (endInclusive - startInclusive) / 2;

            PickFruitTask leftSum = new PickFruitTask(appleTrees, startInclusive, midpoint);
            PickFruitTask rightSum = new PickFruitTask(appleTrees, midpoint + 1, endInclusive);

            // Asynchronous fork()
            rightSum.fork();
            // Execute synchronous compute() then wait on async task with join()
            return leftSum.compute() + rightSum.join();

        }

        protected Integer doCompute() {
            return IntStream.rangeClosed(startInclusive, endInclusive) //
                    .map(i -> appleTrees[i].pickApples()) //
                    .sum();
        }

    }
}
