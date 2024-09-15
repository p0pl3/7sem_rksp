package pr2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Task3 {
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

    public static void main(String[] args) {
        String filePath = "src/pr2/QWE.txt";
        try {
            short checksum = calculateChecksum(filePath);
            System.out.printf("16-битная контрольная сумма файла: 0x%04X%n", checksum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
