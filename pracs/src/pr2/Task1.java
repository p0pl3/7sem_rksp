package pr2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Task1 {
    public static void main(String[] args) {
        String filePath = "src/pr2/task1.txt";
        // Читаем содержимое файла и выводим его
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            System.out.println("Содержимое файла:");
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
