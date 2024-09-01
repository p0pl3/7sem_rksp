package pr3;

import io.reactivex.rxjava3.core.Observable;

import java.util.Random;


class Task21 {
    public static void main(String[] args) {
        Random random = new Random();

        // Создаем поток из 1000 случайных чисел от 0 до 1000 и преобразуем в поток квадратов
        Observable<Integer> squaresStream = Observable.range(0, 1000)
                .map(i -> random.nextInt(1001)) // Генерируем случайные числа от 0 до 1000
                .map(num -> num * num); // Возвращаем квадрат числа

        squaresStream.subscribe(square -> System.out.println("Квадрат: " + square));
    }
}

class Task22 {
    public static void main(String[] args) {
        Random random = new Random();

        // Создаем два потока: один с буквами, другой с цифрами
        Observable<Character> lettersStream = Observable.range(0, 1000)
                .map(i -> (char) ('A' + random.nextInt(26))); // Генерируем случайные буквы от A до Z

        Observable<Integer> digitsStream = Observable.range(0, 1000)
                .map(i -> random.nextInt(10)); // Генерируем случайные цифры от 0 до 9

        // Объединяем буквы и цифры
        Observable<String> combinedStream = Observable.zip(
                lettersStream,
                digitsStream,
                (letter, digit) -> String.valueOf(letter) + digit // Формируем строку (буква + цифра)
        );

        combinedStream.subscribe(combined -> System.out.println("Объединенное: " + combined));
    }
}


class Task23 {
    public static void main(String[] args) {
        Random random = new Random();

        // Создаем поток из 10 случайных чисел
        Observable<Integer> numbersStream = Observable.range(0, 10)
                .map(i -> {
                    int k = random.nextInt(100);
                    System.out.print(k + " ");
                    return k;
                }); // Генерируем случайные числа от 0 до 99

        // Удаляем первые три числа
        Observable<Integer> resultStream = numbersStream.skip(3); // Пропускаем первые 3 элемента
        System.out.println();

        resultStream.subscribe(System.out::println);
    }
}

