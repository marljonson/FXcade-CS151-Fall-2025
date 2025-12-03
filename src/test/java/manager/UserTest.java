package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorWithoutHighScore() {
        User user = new User("alice", "password123");
        assertEquals("alice", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(0, user.getHighScore()); // default highScore
    }
}
