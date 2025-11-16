package manager;

public class User {
    private String username;
    private String password;
    private int highScore;

    // Constructors
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
    }

    public User(String username, String password, int highScore) {
        this.username = username;
        this.password = password;
        this.highScore = highScore;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getHighScore() { return highScore; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
}