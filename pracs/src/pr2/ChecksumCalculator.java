package pr2;

import java.io.*;
import java.nio.ByteBuffer;

public class ChecksumCalculator {

    public static int calculateChecksum(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] buffer = fileToBytes(fileInputStream);
            int checksum = calculateChecksum(buffer);
            fileInputStream.close();
            return checksum;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static byte[] fileToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // Размер буфера для чтения файла

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    private static int calculateChecksum(byte[] data) {
        int checksum = 0;
        ByteBuffer buffer = ByteBuffer.wrap(data);

        while (buffer.hasRemaining()) {
            short value = buffer.getShort();
            checksum = (checksum + value) & 0xFFFF; // Для ограничения контрольной суммы 16 битами
        }

        return checksum;
    }

    public static void main(String[] args) {
        String filePath = "src/pr2/QWE.txt";
        int checksum = calculateChecksum(filePath);
        System.out.println("16-битная контрольная сумма файла: " + checksum);
        System.out.println(Integer.toHexString(checksum));
    }
}
