package pr1;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class File {
    String type;
    int size;

    public File(String type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public String toString() {
        return "File{" +
                "type='" + type + '\'' +
                ", size=" + size +
                '}';
    }
}

class FileGenerator extends Thread {
    private final BlockingQueue<File> fileQueue;
    private final Random random = new Random();
    private static final String[] FILE_TYPES = {"XML", "JSON", "XLS"};

    public FileGenerator(BlockingQueue<File> fileQueue) {
        this.fileQueue = fileQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(random.nextInt(901) + 100); // Задержка от 100 до 1000 мс
                String fileType = FILE_TYPES[random.nextInt(FILE_TYPES.length)];
                int size = random.nextInt(91) + 10; // Размер от 10 до 100
                File file = new File(fileType, size);
                fileQueue.put(file);
                System.out.println("Сгенерирован файл: " + file);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

class FileProcessor extends Thread {
    private final BlockingQueue<File> fileQueue;
    private final String fileType;

    public FileProcessor(BlockingQueue<File> fileQueue, String fileType) {
        this.fileQueue = fileQueue;
        this.fileType = fileType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                File file = fileQueue.take(); // Блокируем до получения файла
                if (file.type.equals(fileType)) {
                    // Обработка файла
                    int processingTime = file.size * 7;
                    System.out.println("Обработка файла " + file + " ...");
                    Thread.sleep(processingTime); // Эмулируем время обработки в мс
                    System.out.println("Файл " + file + " обработан. Время обработки: "
                            + processingTime + "мс.");
                } else {
                    // Если тип файла не соответствует, возвращаем его обратно в очередь
                    fileQueue.put(file);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

public class Task3 {
    private static final int MAX_QUEUE_SIZE = 5;

    public static void main(String[] args) {
        BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
        // Создание генератора файлов
        FileGenerator generator = new FileGenerator(fileQueue);
        // Создание обработчиков для каждого типа файлов
        FileProcessor xmlProcessor = new FileProcessor(fileQueue, "XML");
        FileProcessor jsonProcessor = new FileProcessor(fileQueue, "JSON");
        FileProcessor xlsProcessor = new FileProcessor(fileQueue, "XLS");

        generator.start();
        xmlProcessor.start();
        jsonProcessor.start();
        xlsProcessor.start();

        try {
            // Даем системе поработать некоторое время
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Завершение работы
        generator.interrupt();
        xmlProcessor.interrupt();
        jsonProcessor.interrupt();
        xlsProcessor.interrupt();

        System.out.println("Завершение работы системы.");
    }
}
