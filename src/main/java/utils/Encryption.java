package utils;

public class Encryption {
    
    private static final int SHIFT = 3;

    // Caesar cipher encryption
    public static String encrypt(String text) {
        String encrypted = "";
        for (char ch : text.toCharArray()) {
            encrypted += (char) (ch + SHIFT);
        }
        return encrypted;
    }
}