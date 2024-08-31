package pr2;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Task2 {
    public static void main(String[] args) throws IOException {
        File sourceFile = new File("src/pr2/task2input.txt");
        File destFile = new File("src/pr2/task2output.txt");

        long startTime = System.nanoTime();
        copyFileStream(sourceFile, destFile);
        long endTime = System.nanoTime();
        System.out.println("Stream method time: " + (endTime - startTime) + " ns");

        // Сбросить файл
        destFile.delete();

        startTime = System.nanoTime();
        copyFileChannel(sourceFile, destFile);
        endTime = System.nanoTime();
        System.out.println("Channel method time: " + (endTime - startTime) + " ns");

        // Сбросить файл
//        destFile.delete();
//
//        startTime = System.nanoTime();
//        copyFileCommonsIO(sourceFile, destFile);
//        endTime = System.nanoTime();
//        System.out.println("Commons IO method time: " + (endTime - startTime) + " ns");

        // Сбросить файл
        destFile.delete();

        startTime = System.nanoTime();
        copyFileFilesClass(sourceFile, destFile);
        endTime = System.nanoTime();
        System.out.println("Files class method time: " + (endTime - startTime) + " ns");
    }

    public static void copyFileStream(File source, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void copyFileChannel(File source, File destination) {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(destination).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

//    public static void copyFileCommonsIO(File source, File destination) throws IOException {
//        FileUtils.copyFile(source, destination);
//    }

    public static void copyFileFilesClass(File source, File destination) throws IOException {
        Path sourcePath = source.toPath();
        Path destPath = destination.toPath();
        Files.copy(sourcePath, destPath);
    }

}
