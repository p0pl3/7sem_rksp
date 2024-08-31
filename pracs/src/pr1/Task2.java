package pr1;

import java.util.Scanner;
import java.util.concurrent.*;


public class Task2 {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите число для возведения в квадрат (или 'exit' для выхода):");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                int number = Integer.parseInt(input);
                Future<Integer> futureResult = executor.submit(() -> {
                    // Имитация обработки с задержкой от 1 до 5 секунд
                    int delay = ThreadLocalRandom.current().nextInt(1, 6);
                    Thread.sleep(delay * 1000); // задержка в миллисекундах
                    return number * number; // Возводим число в квадрат
                });

                // Вывод результата, когда задача завершится
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
