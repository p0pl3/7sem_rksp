package pr2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task4 {

    private static Map<Path, List<String>> fileContentsMap = new HashMap<>();
    private static Map<Path, String> fileHashes = new HashMap<>();

    public static short calculateChecksum(String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             FileChannel fileChannel = fileInputStream.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            short checksum = 0;
            while (fileChannel.read(buffer) != -1) {
                buffer.flip(); // Переключаем буфер в режим чтения
                while (buffer.hasRemaining()) {
                    checksum ^= buffer.get(); // Выполняем XOR над байтами
                }
                buffer.clear(); // Очищаем буфер для следующего чтения
            }
            return checksum;
        }
    }

    public static void watchDirectory(Path path) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

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
                        String hash = fileHashes.get(fullPath);
                        if (hash != null) {
                            System.out.println("Хеш-сумма удаленного файла: " +
                                    hash);
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

    private static void firstObserve(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    fileContentsMap.put(filePath, readLinesFromFile(filePath));
                    calculateFileHash(filePath);
                }
            }
        }
    }

    private static void calculateFileHash(Path filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream is = Files.newInputStream(filePath);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                while (dis.read() != -1) ;
                String hash = bytesToHex(md.digest());
                fileHashes.put(filePath, hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }



    private static void printFileChanges(Path filePath) throws IOException {
        List<String> newFileContents = readLinesFromFile(filePath);
        List<String> oldFileContents = fileContentsMap.get(filePath);
        if (oldFileContents != null) {
            List<String> addedLines = newFileContents.stream()
                    .filter(line -> !oldFileContents.contains(line))
                    .toList();
            List<String> deletedLines = oldFileContents.stream()
                    .filter(line -> !newFileContents.contains(line))
                    .toList();
            if (!addedLines.isEmpty()) {
                System.out.println("Добавленные строки в файле " + filePath +
                        ":");
                addedLines.forEach(line -> System.out.println("+ " + line));
            }
            if (!deletedLines.isEmpty()) {
                System.out.println("Удаленные строки из файла " + filePath +
                        ":");
                deletedLines.forEach(line -> System.out.println("- " + line));
            }
        }
        // Обновляем хранимое содержимое файла
        fileContentsMap.put(filePath, newFileContents);
    }

    private static List<String> readLinesFromFile(Path filePath) throws
            IOException, FileSystemException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    public static void main(String[] args) {
        Path path = Paths.get("src/pr2"); // Замените на путь к вашему каталогу
        watchDirectory(path);
    }
}
