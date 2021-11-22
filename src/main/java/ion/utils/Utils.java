package ion.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') ? true : false;
    }

    public static boolean isDigit(char c) {
        return (c >= '0' && c <= '9') ? true : false;
    }

    public static boolean isOperatorChar(char c) {
        return (c == '+' || c == '-' || c == '*' || c =='%');
    }

    public static int typenameToInt(String name) {
        int t = 0;
        for(int i = 0; i < name.length(); i++) t += (int) name.charAt(i);
        return t;
    }

    public static String readFileToString(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }

    public static String readFileToString(File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    public static byte[] readFile(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath));
    }

    public static byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
    }

    public static void writeFileFromString(String filepath, String str) throws IOException {
        Files.write(Paths.get(filepath), str.getBytes());
    }

    public static void writeFileFromString(File file, String str) throws IOException {
        Files.write(Paths.get(file.getAbsolutePath()), str.getBytes());
    }

    public static void writeFile(String filepath, byte[] bytes) throws IOException {
        Files.write(Paths.get(filepath), bytes);
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        Files.write(Paths.get(file.getAbsolutePath()), bytes);
    }

    // Internal utils

    public static byte getByteSize(String type) {
        if(type.endsWith("*")) return 8;
        switch(type) {
            case "uint64": return 8;
            case "uint32": return 4;
            case "uint16": return 2;
            case "uint8": return 1;
            default:
                System.err.println("[Parser] Unknown bytesize.");
                System.exit(1);
                return 0; // Unreachable
        }
    }

}
