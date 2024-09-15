package pr2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;

public class Task4 {
    public static int calculateChecksum(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int checksum = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                while (byteBuffer.hasRemaining()) {
                    checksum += byteBuffer.get() & 0xFF;
                    checksum &= 0xFFFF;
                }
            }
            return checksum;
        }
    }

    public static void watchDirectory(Path path) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    Path fullPath = path.resolve(fileName);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("Создан новый файл: " + fileName);

                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("Изменен файл: " + fileName);
                        printFileChanges(fullPath);

                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Удален файл: " + fileName);
                        File deletedFile = fullPath.toFile();
                        System.out.println("Размер файла: " + deletedFile.length() + " байт");
                        try {
                            int checksum = calculateChecksum(deletedFile.getAbsolutePath());
                            System.out.printf("Контрольная сумма: 0x%04X%n", checksum);
                        } catch (IOException e) {
                            System.out.println("Не удалось вычислить контрольную сумму.");
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printFileChanges(Path filePath) {
        // Загрузим текущее содержание файла
        // Для реализации этого необходимо хранить предыдущее состояние
        // в простом случае можем сохранить последнее прочитанное состояние в памяти на время выполнения программы

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String currentLine;
            StringBuilder currentContent = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                currentContent.append(currentLine).append(System.lineSeparator());
            }

            // Здесь нам нужно сравнить с предыдущим содержанием
            // Предположим, что мы имели какое-то предыдущее состояние в переменной previousContent
            // Это необходимо реализовать, храня предыдущее состояние в коллекции или файле

            // Реализация различий не включена в данный код для простоты
            System.out.println("Текущее содержание файла:\n" + currentContent.toString());
            // Выведите сравнение с предыдущим содержанием (если было сохранено)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get("E:/study/7sem/rksp/praccks/pracs/src/pr2"); // Замените на путь к вашему каталогу
        watchDirectory(path);
    }
}
