package pr2;


import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public class Task2 {
    public static void main(String[] args) throws IOException {
        File sourceFile = new File("src/pr2/task2input.txt");
        File destFile = new File("src/pr2/task2output.txt");

        destFile.delete();
        long startTime = System.currentTimeMillis();
        copyFileStream(sourceFile, destFile);
        long endTime = System.currentTimeMillis();
        System.out.println("Stream method time: " + (endTime - startTime) + " ns");

        destFile.delete();
        startTime = System.currentTimeMillis();
        copyFileChannel(sourceFile, destFile);
        endTime = System.currentTimeMillis();
        System.out.println("Channel method time: " + (endTime - startTime) + " ns");


        destFile.delete();
        startTime = System.currentTimeMillis();
        copyUsingApacheCommonsIO(sourceFile, destFile);
        endTime = System.currentTimeMillis();
        System.out.println("Apache commons time: " + (endTime - startTime) + " ns");


        destFile.delete();
        startTime = System.currentTimeMillis();
        copyFileFilesClass(sourceFile, destFile);
        endTime = System.currentTimeMillis();
        System.out.println("Files class method time: " + (endTime - startTime) + " ns");
    }

    public static void copyFileStream(File source, File destination) throws IOException {
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fis.close();
        fos.close();
    }

    public static void copyFileChannel(File source, File destination) throws IOException {
        FileChannel sourceChannel = new FileInputStream(source).getChannel();
        FileChannel destChannel = new FileOutputStream(destination).getChannel();
        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        sourceChannel.close();
        destChannel.close();

    }

    private static void copyUsingApacheCommonsIO(File source, File destination) throws IOException {
        FileUtils.copyFile(source, destination);
    }


    public static void copyFileFilesClass(File source, File destination) throws IOException {
        Path sourcePath = source.toPath();
        Path destPath = destination.toPath();
        Files.copy(sourcePath, destPath);
    }
}
