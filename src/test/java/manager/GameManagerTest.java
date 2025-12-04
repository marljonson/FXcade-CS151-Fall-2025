package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import utils.Encryption;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private AccountManager manager;

    @BeforeEach
    void setUp(){
        manager = new AccountManager();
        manager.getUsers().clear();
    }

    @Test
    void signInUserNotFound(){
        assertEquals(AccountManager.loginStatus.USER_NOT_FOUND, manager.signIn("unknown", "password"));
        assertNull(manager.getActiveUser());
    }

    @Test
    void signInWrongPassword(){
        String encrypted = Encryption.encrypt("correctpw");
        manager.getUsers().put("bob", new User("bob", encrypted));
        assertEquals(AccountManager.loginStatus.WRONG_PASSWORD, manager.signIn("bob", "wrongpw"));
        assertNull(manager.getActiveUser());
    }

}
