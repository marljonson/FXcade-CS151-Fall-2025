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

import utils.Encryption;

public class AccountManager {

    private static final int MAX_TOP_SCORES = 5;

    private Map<String, User> users = new HashMap<>();
    private User activeUser;

    public enum loginStatus {
        SUCCESS,
        USER_NOT_FOUND,
        WRONG_PASSWORD
    }

    public enum signUpStatus {
        SUCCESS,
        USER_TAKEN,
        PASSWORD_LENGTH,
        COLON_SYMBOL,
        FILE_ERROR
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
            System.out.println("Error reading from file [user_accounts.txt]: " + e.getMessage());
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

        if (signInUser.getPassword().equals(Encryption.encrypt(password))) {
            System.out.println("User " + username + " successfully logged in.");
            setActiveUser(signInUser);
            return loginStatus.SUCCESS;
        } else {
            System.out.println("Password does not match. Please try again.");
            return loginStatus.WRONG_PASSWORD;
        }
    }

    // Create Account
    public signUpStatus createUser(String username, String password) {
        // Check if username already exists
        if (users.containsKey(username)) {
            System.out.println("Username is taken. Please try again with another username.");
            return signUpStatus.USER_TAKEN;
        }

        // Check if username/password contains character ':'
        if (username.contains(":") || password.contains(":")) {
            System.out.println("Username and password cannot contain ':' characters.");
            return signUpStatus.COLON_SYMBOL;
        }

        // Password length check
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters.");
            return signUpStatus.PASSWORD_LENGTH;
        }

        // Encrypt password
        String encryptedPassword = Encryption.encrypt(password);

        // Create new user with encrypted password and default high score of 0
        User newUser = new User(username, encryptedPassword);

        // Add new user to the hash map
        users.put(username, newUser);

        // Set the new user as activeUser
        setActiveUser(newUser);

        // Save details to user_accounts.txt
        Path filePath = Paths.get("data/user_accounts.txt");
        String savedOutput = username + ":" + encryptedPassword + ":" + newUser.getHighScore();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), true))) {

            // Add newline BEFORE writing ONLY if file already has content
            if (Files.size(filePath) > 0) {
                writer.newLine();
            }

            writer.write(savedOutput);
            createHighScore(username);

            return signUpStatus.SUCCESS;

        } catch (IOException e) {
            System.out.println("Error saving new user to file [user_accounts.txt]: " + e.getMessage());
            return signUpStatus.FILE_ERROR;
        }
    }

    public void createHighScore(String username) {
        Path filePath = Paths.get("data/high_scores.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString(), true))) {

            if (Files.size(filePath) > 0) {
                writer.newLine();
            }

            StringBuilder snakeHighScores = new StringBuilder();
            snakeHighScores.append(username).append(":snake");
            for (int i = 0; i < MAX_TOP_SCORES; i++) {
                snakeHighScores.append(":0");
            }

            StringBuilder blackJackHighScores = new StringBuilder();
            blackJackHighScores.append(username).append(":blackjack");
            for (int i = 0; i < MAX_TOP_SCORES; i++) {
                blackJackHighScores.append(":0");
            }

            writer.write(snakeHighScores.toString());
            writer.newLine();
            writer.write(blackJackHighScores.toString());

        } catch (IOException e) {
            System.out.println("Error creating high scores for user [" + username + "]: " + e.getMessage());
        }
    }
}