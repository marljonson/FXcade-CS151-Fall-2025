package utils;

import org.junit.jupiter.api.BeforeEach;
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
}
