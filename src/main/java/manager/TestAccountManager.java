package manager;

public class TestAccountManager {

    public static void main(String[] args) {

        System.out.println("=== TESTING ACCOUNT MANAGER ===");

        // 1. Create manager + load existing users
        AccountManager manager = new AccountManager();
        System.out.println("Loaded users: " + manager.getUsers().size());
        System.out.println("Usernames: " + manager.getUsers().keySet());

        // 2. Test login with existing user
        System.out.println("\n--- Test Login: existing user (alice) ---");
        boolean login1 = manager.signIn("alice", "password123");
        System.out.println("Login success: " + login1);
        if (login1 && manager.getActiveUser() != null) {
            System.out.println("Active user: " + manager.getActiveUser().getUsername());
            System.out.println("High score: " + manager.getActiveUser().getHighScore());
        }

        // 3. Test login with wrong password
        System.out.println("\n--- Test Login: wrong password ---");
        boolean login2 = manager.signIn("alice", "wrongpass");
        System.out.println("Login success: " + login2);

        // 4. Create new user
        System.out.println("\n--- Creating new user: abc ---");
        boolean created = manager.createUser("abc", "mypassword");
        System.out.println("Created: " + created);

        // 5. Login new user
        System.out.println("\n--- Login new user: charlie ---");
        boolean login3 = manager.signIn("charlie", "mypassword");
        System.out.println("Login success: " + login3);

        // 6. Reload to confirm file writing works
        System.out.println("\n--- Reload AccountManager (file persistence test) ---");
        AccountManager manager2 = new AccountManager();
        System.out.println("Loaded users: " + manager2.getUsers().size());
        System.out.println("Usernames now: " + manager2.getUsers().keySet());
    }
}
