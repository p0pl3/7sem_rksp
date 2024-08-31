package pr2;

import java.io.*;

public class Task1 {
    public static void main(String[] args) {
        try (FileInputStream fin = new FileInputStream("E:/study/7sem/rksp/pracs/src/pr2/task1.txt")) {
            int i = -1;
            while ((i = fin.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
