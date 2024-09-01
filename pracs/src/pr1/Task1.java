package pr1;

import java.util.concurrent.*;


public class Task1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] nums = new int[10000];
        for (int i = 0; i < nums.length; i++)
            nums[i] = ((int) Math.floor(Math.random() * 10000) + 1);

        long startTime, endTime;
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();
        int minNum = findMinSeq(nums);
        endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Последовательный метод\nМинимальный элемент: " + minNum + "\nВремя: " + (endTime - startTime) + " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");

        runtime.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();
        minNum = findMinThreads(nums, 20);
        endTime = System.currentTimeMillis();
        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Многопоточный метод\nМинимальный элемент: " + minNum + "\nВремя: " + (endTime - startTime) + " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");

        runtime.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.currentTimeMillis();
        minNum = findMinForkJoin(nums);
        endTime = System.currentTimeMillis();
        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Метод ForkJoin\nМинимальный элемент: " + minNum + "\nВремя: " + (endTime - startTime) + " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");
    }

    public static int findMinSeq(int[] nums) {
        int minNum = Integer.MAX_VALUE;
        for (int i : nums) {
            if (i < minNum) {
                minNum = i;
                try {
                    Thread.sleep(1); // Задержка 1 мс
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return minNum;
    }

    public static int findMinThreads(int[] array, int numThreads) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = (int) Math.ceil((double) array.length / numThreads);
        Future<Integer>[] futures = new Future[numThreads];

        // Создаем потоки для поиска минимума в каждой части массива
        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, array.length);
            futures[i] = executor.submit(() -> {
                int min = Integer.MAX_VALUE;
                for (int j = start; j < end; j++) {
                    if (j < array.length) {
                        Thread.sleep(1);
                        min = Math.min(min, array[j]);
                    }
                }
                return min;
            });
        }

        // Ждем завершения всех потоков и находим общий минимум
        int overallMin = Integer.MAX_VALUE;
        for (Future<Integer> future : futures) {
            overallMin = Math.min(overallMin, future.get());
        }

        executor.shutdown();
        return overallMin;
    }

    public static int findMinForkJoin(int[] array) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        return pool.invoke(new MinTask(array, 0, array.length));
    }

    static class MinTask extends RecursiveTask<Integer> {
        private final int[] array;
        private final int start;
        private final int end;
        private final static int THRESHOLD = 5; // Порог для деления задачи

        public MinTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            // Если размер задачи мал, ищем минимум последовательно
            if (end - start <= THRESHOLD) {
                int min = Integer.MAX_VALUE;
                for (int i = start; i < end; i++) {
                    min = Math.min(min, array[i]);
                }
                return min;
            } else {
                // Делим задачу на две части
                int mid = (start + end) / 2;
                MinTask leftTask = new MinTask(array, start, mid);
                MinTask rightTask = new MinTask(array, mid, end);

                // Запускаем подзадачи параллельно
                leftTask.fork();
                int rightResult = rightTask.compute(); // Выполняем правую задачу в текущем потоке
                int leftResult = leftTask.join(); // Ждем завершения левой задачи

                // Соединяем результаты
                return Math.min(leftResult, rightResult);
            }
        }
    }
}