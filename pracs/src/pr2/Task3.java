package pr2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Task3 {
    public static int calculateChecksum(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int checksum = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    checksum += buffer[i] & 0xFF; // Приводим байт к беззнаковому значению
                    checksum &= 0xFFFF; // Держим только 16 младших бит
                }
            }
            return checksum;
        }
    }

    private static int sum(ByteBuffer bb) {
        int sum = 0;
        while (bb.hasRemaining()) {
            if ((sum & 1) != 0)
                sum = (sum >> 1) + 0x8000;
            else
                sum >>= 1;
            sum += bb.get() & 0xff;
            sum &= 0xffff;
        }
        return sum;
    }

    // Compute and print a checksum for the given file

    private static void sum(File f) throws IOException {

        // Open the file and then get a channel from the stream
        try (
                FileInputStream fis = new FileInputStream(f);
                FileChannel fc = fis.getChannel()) {

            // Get the file's size and then map it into memory
            int sz = (int) fc.size();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

            // Compute and print the checksum
            int sum = sum(bb);
            int kb = (sz + 1023) / 1024;
            String s = Integer.toString(sum);
            System.out.println(sum + "\t" + kb + "\t" + f);
            System.out.println(Integer.toHexString(sum));
        }
    }

    public static String check_sum(String path) throws IOException {
        // calculate the sum,
        int sum = 0;
        // read file stream
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        // Byte array, 2 bytes, representing 16 bits
        byte[] b = new byte[2];
        // number of bytes read
        int n;
        while ((n = inputStream.read(b)) != -1) {
            if (n == 1) {
                // The number of bytes read is odd, and 8 0s (1 byte) are added
                sum += b[0] << 8;
            } else {
                // The number of bytes read is even
                sum += (b[0] << 8) + b[1];
            }
        }
        inputStream.close();
        // Carry processing
        if (sum > 0xffff) {
            sum = (sum / 65536 + sum % 65536);
        }
        // Invert code java int occupies 32 bits, and the check code is 16 bits
        return Integer.toHexString(~sum).substring(4);
    }


    public static void main(String[] args) {
        String filePath = "src/pr2/QWE.txt";
        try {
            int checksum = calculateChecksum(filePath);
            System.out.printf("16-битная контрольная сумма файла: 0x%04X%n", checksum);
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        System.out.println("\n");
//
//        File f = new File(filePath);
//        try {
//            sum(f);
//        } catch (IOException e) {
//            System.err.println(f + ": " + e);
//        }

//        try {
//            System.out.println("контрольная сумма:" + check_sum(filePath));
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("An exception has occurred, exited!");
//        }
    }
}
