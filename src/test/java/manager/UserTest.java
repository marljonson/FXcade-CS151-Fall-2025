package manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorWithoutHighScore() {
        User user = new User("alice", "password123");
        assertEquals("alice", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(0, user.getHighScore()); // 0 is the default highScore
    }

    @Test
    void testConstructorWithHighScore() {
        User user = new User("cheshire", "password123", 100);
        assertEquals("cheshire", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(100, user.getHighScore());
    }

    @Test
    void testSetters() {
        User user = new User("happyhatter", "underworld", 200);
        user.setUsername("madhatter");
        user.setPassword("wonderland");
        user.setHighScore(300);
        assertEquals("madhatter", user.getUsername());
        assertEquals("wonderland", user.getPassword());
        assertEquals(300, user.getHighScore());
    }
}
