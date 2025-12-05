package utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionTest {
    
    @Test
    void testEncryptNormalString() {
        String input = "hello";
        String expected = "khoor";
        String actual = Encryption.encrypt(input);
        assertEquals(expected, actual, "The encryption result should match the expected value.");
    }

    @Test
    void testEncryptEmptyString() {
        String input = "";
        String expected = ""; // encryption of empty string is still empty string
        String actual = Encryption.encrypt(input);
        assertEquals(expected, actual, "Encrypting an empty string should return an empty string.");
    }

    @Test
    void testEncryptSpecialCharacters() {
        String input = "hello world!";
        String expected = "khoor#zruog$"; // password encryption (Caesar; +3 shift) should handle spaces
        String actual = Encryption.encrypt(input);
        assertEquals(expected, actual, "The encryption result should handle spaces and special characters.");
    }

    @Test 
    void testEncryptMixedCase() {
        String input = "Hello World!";
        String expected = "Khoor#Zruog$";
        String actual = Encryption.encrypt(input);
        assertEquals(expected, actual, "The encryption result should handle uppercase letters.");
    }
}
