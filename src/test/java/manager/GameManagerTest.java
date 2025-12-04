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
        manager.getUsers().put("Bob", new User("Bob", encrypted));
        assertEquals(AccountManager.loginStatus.WRONG_PASSWORD, manager.signIn("Bob", "wrongpw"));
        assertNull(manager.getActiveUser());
    }

    @Test
    void successfulSignInSetsActiveUser(){
        String encrypted = Encryption.encrypt("correctpw");
        User user = new User("Bob", encrypted);
        manager.getUsers().put("Bob", user);

        assertEquals(AccountManager.loginStatus.SUCCESS, manager.signIn("Bob", "correctpw"));
        assertEquals(user, manager.getActiveUser());
    }

    @Test
    void createUserPasswordTooShort(){
        AccountManager.signUpStatus status = manager.createUser("Alice", "short");
        assertEquals(AccountManager.signUpStatus.PASSWORD_LENGTH, status);
        assertFalse(manager.getUsers().containsKey("alice"));
    }

}
