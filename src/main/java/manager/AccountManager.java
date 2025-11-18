package manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManager {

    private Map<String, User> users = new HashMap<>();
    private User activeUser;
    public enum loginStatus {
        SUCCESS,
        USER_NOT_FOUND,
        WRONG_PASSWORD
    }

    // Constructor
    public AccountManager() {
        // Load users from user_accounts.txt
        readFromFile();
    }

    // Getters and setters
    public Map<String, User> getUsers() {
        return users;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void setActiveUser(String username) {

    }

    // Load users from user_accounts.txt
    private void readFromFile() {
        // Get relative path
        Path filePath = Paths.get("data/user_accounts.txt");

        if (!Files.exists(filePath)) {
            System.out.println("File does not exist");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                String[] split = line.split(":");
                String username = split[0];
                String password = split[1];
                int highScore = Integer.parseInt(split[2]);
                users.put(username, new User(username, password, highScore));
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: user_accounts.txt");
        }
    }

    // Login
    public loginStatus signIn(String username, String password) {
        // Check if user exists
        if (!users.containsKey(username)) {
            System.out.println("User " + username + " not found");
            System.out.println("Please try again or create an account.");
            return loginStatus.USER_NOT_FOUND;
        }

        User signInUser = users.get(username);

        if (signInUser.getPassword().equals(password)) {
            System.out.println("User " + username + " successfully logged in.");
            setActiveUser(signInUser);
            return loginStatus.SUCCESS;
        } else {
            System.out.println("Password does not match. Please try again.");
            return loginStatus.WRONG_PASSWORD;
        }
    }

    // Create Account
    public boolean createUser(String username, String password) {
        // Check if username already exists
        if (users.containsKey(username)) {
            System.out.println("Username is taken. Please try again with another username.");
            return false;
        }

        // Check if username/password contains character ':'
        if (username.contains(":") || password.contains(":")) {
            System.out.println("Username and password cannot contain ':' characters.");
            return false;
        }

        // Password length check
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters.");
            return false;
        }

        // Create new user with default high score of 0
        User newUser = new User(username, password);

        // Add new user to the hash map
        users.put(username, newUser);

        // Set the new user as activeUser
        setActiveUser(newUser);

        // Save details to user_accounts.txt
        Path filePath = Paths.get("data/user_accounts.txt");
        String savedOutput = username + ":" + password + ":" + newUser.getHighScore();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), true));
            writer.write(savedOutput);
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving new user to file: user_accounts.txt");
            return false;
        }
    }
}
