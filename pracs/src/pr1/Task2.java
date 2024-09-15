package pr1;

import java.util.Scanner;
import java.util.concurrent.*;


public class Task2 {
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите число для возведения в квадрат (или 'exit' для выхода):");
        while (true) {
            try {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit"))
                    break;
                int number = Integer.parseInt(input);
                Future<Integer> futureResult = executor.submit(() -> {
                    int delay = ThreadLocalRandom.current().nextInt(1, 6);
                    Thread.sleep(delay * 1000);
                    return number * number;
                });
                CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return futureResult.get(); // Получаем результат из Future
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Ошибка вычисления: " + e.getMessage());
                        return null;
                    }
                });

                completableFuture.thenAccept(result -> {
                    if (result != null) {
                        System.out.println("Результат: " + result);
                    }
                });
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите корректное число.");
            }
        }
        executor.shutdown();
        scanner.close();
        System.out.println("Выход из программы.");
    }
}
