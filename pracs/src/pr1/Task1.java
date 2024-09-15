package pr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Task1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Integer> nums = generateArray10000();

        long startTime, endTime, memoryBefore, memoryAfter;
        int minNum;
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.nanoTime();
        minNum = findMinSeq(nums);
        endTime = System.nanoTime();
        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Последовательный метод\nМинимальный элемент: "
                + minNum + "\nВремя: " + (endTime - startTime) / 1000000 +
                " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");

        runtime.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.nanoTime();
        minNum = findMinThreads(nums);
        endTime = System.nanoTime();
        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Многопоточный метод\nМинимальный элемент: "
                + minNum + "\nВремя: " + (endTime - startTime) / 1000000 +
                " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");

        runtime.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.nanoTime();
        minNum = findMinForkJoin(nums);
        endTime = System.nanoTime();
        memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Метод ForkJoin\nМинимальный элемент: "
                + minNum + "\nВремя: " + (endTime - startTime) / 1000000 +
                " мс\nПамять: " + (memoryAfter - memoryBefore) + " байт\n");
    }

    public static List<Integer> generateArray10000() { // генерация 10000 рандомных чисел типа Integer в листе
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int randomNumber = random.nextInt();
            list.add(randomNumber);
        }
        return list;
    }


    public static int findMinSeq(List<Integer> nums) throws InterruptedException {
        int minNum = Integer.MAX_VALUE;
        for (int i : nums) {
            Thread.sleep(1); // Задержка 1 мс
            if (i < minNum) {
                minNum = i;
            }
        }
        return minNum;
    }

    public static int findMinThreads(List<Integer> list) throws InterruptedException, ExecutionException {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        int chunkSize = list.size() / numberOfThreads;
        List<Callable<Integer>> tasks = new ArrayList<>();

        // Создаем потоки для поиска минимума в каждой части массива
        for (int i = 0; i < numberOfThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numberOfThreads - 1) ? list.size() : (i + 1) * chunkSize;

            tasks.add(() -> findMinSeq(list.subList(start, end)));
        }

        List<Future<Integer>> futures = executor.invokeAll(tasks);

        // Ждем завершения всех потоков и находим общий минимум
        int overallMin = Integer.MAX_VALUE;
        for (Future<Integer> future : futures) {
            int partialMin = future.get();
            Thread.sleep(1);
            if (partialMin < overallMin) {
                overallMin = partialMin;
            }
        }


        executor.shutdown();
        return overallMin;
    }

    public static int findMinForkJoin(List<Integer> list) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        MinTask task = new MinTask(list, 0, list.size());

        return forkJoinPool.invoke(task);
    }

    static class MinTask extends RecursiveTask<Integer> {
        private final List<Integer> list;
        private final int start;
        private final int end;
        private final static int THRESHOLD = 1000; // Порог для деления задачи

        public MinTask(List<Integer> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            // Если размер задачи мал, ищем минимум последовательно
            if (end - start <= THRESHOLD) {
                try {
                    return findMinSeq(list.subList(start, end));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Делим задачу на две части
                int mid = (start + end) / 2;
                MinTask leftTask = new MinTask(list, start, mid);
                MinTask rightTask = new MinTask(list, mid, end);

                // Запускаем подзадачи параллельно
                leftTask.fork();
                int rightResult = rightTask.compute(); // Выполняем правую задачу в текущем потоке
                int leftResult = leftTask.join(); // Ждем завершения левой задачи
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Соединяем результаты
                return Math.min(leftResult, rightResult);
            }
        }
    }
}