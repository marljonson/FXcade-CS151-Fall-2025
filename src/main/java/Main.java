package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import manager.AccountManager;
import ui.BlackjackController;

import java.nio.file.*;
import java.util.*;

public class Main extends Application {

    private AccountManager accountManager;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;
    private MediaPlayer sfxPlayer;

    private Stage primaryStage;
    private BorderPane rootPane;
    private Scene mainScene;
    private Scene loginScene;
    private String currentUsername = "";

    public Main() {
        this.accountManager = new AccountManager();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Music setup
        try {
            var url = getClass().getResource("/audio/catherine.mp3");
            if (url != null) {
                Media media = new Media(url.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
        } catch (Exception e) {
            System.out.println("Failed to load music: " + e.getMessage());
        }
        try {
            var popUrl = getClass().getResource("/audio/chime.mp3");
            if (popUrl != null) {
                Media popMedia = new Media(popUrl.toExternalForm());
                sfxPlayer = new MediaPlayer(popMedia);
            }
        } catch (Exception e) {
            System.out.println("Failed to load SFX");
        }

        createLoginAndSignupScenes();
        createMainAppStructure();

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("FXcade Game Manager");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    private void createLoginAndSignupScenes() {
        // === LOGIN ===
        Label welcomeMessage = new Label("Welcome to FXcade!");
        welcomeMessage.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");

        TextField loginUsernameField = new TextField();
        loginUsernameField.setPromptText("Username");
        TextField loginPasswordField = new TextField();
        loginPasswordField.setPromptText("Password");
        Label loginMessageLabel = new Label();
        loginMessageLabel.setTextFill(Color.RED);
        Button loginButton = new Button("Sign In");
        Button createAccountButton = new Button("Sign Up");

        VBox loginBox = new VBox(10, welcomeMessage,
                new Label("Username: "), loginUsernameField,
                new Label("Password: "), loginPasswordField,
                loginButton, loginMessageLabel, new Label("Don't have an account yet?"), createAccountButton);
        loginBox.setPadding(new Insets(30));
        loginBox.setAlignment(Pos.CENTER);

        // === SIGN UP ===
        TextField signupUsernameField = new TextField();
        signupUsernameField.setPromptText("Username");
        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");
        Label signUpMessageLabel = new Label();
        signUpMessageLabel.setTextFill(Color.RED);
        Button signUpButton = new Button("Sign up");
        Button signUpBackButton = new Button("Back");

        VBox signupBox = new VBox(10,
                new Label("Create Account"),
                new Label("Username: "), signupUsernameField,
                new Label("Password: "), signupPasswordField,
                signUpButton, signUpMessageLabel, signUpBackButton);
        signupBox.setPadding(new Insets(30));
        signupBox.setAlignment(Pos.CENTER);
        Scene signupScene = new Scene(signupBox, 700, 500);

        loginButton.setOnAction(e -> {
            String user = loginUsernameField.getText().trim();
            String pass = loginPasswordField.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                loginMessageLabel.setText("Please fill both fields");
                return;
            }
            var status = accountManager.signIn(user, pass);
            if (status == AccountManager.loginStatus.SUCCESS) {
                if (sfxPlayer != null) { sfxPlayer.stop(); sfxPlayer.play(); }
                currentUsername = user;
                primaryStage.setScene(mainScene);
                showMainMenu();
            } else {
                loginMessageLabel.setText(status == AccountManager.loginStatus.USER_NOT_FOUND ?
                        "User not found" : "Wrong password");
            }
        });

        createAccountButton.setOnAction(e -> primaryStage.setScene(signupScene));
        signUpButton.setOnAction(e -> {
            String u = signupUsernameField.getText().trim();
            String p = signupPasswordField.getText();
            var status = accountManager.createUser(u, p);
            signUpMessageLabel.setTextFill(status == AccountManager.signUpStatus.SUCCESS ? Color.GREEN : Color.RED);
            signUpMessageLabel.setText(switch (status) {
                case SUCCESS -> "Account created! Please sign in.";
                case USER_TAKEN -> "Username taken";
                case PASSWORD_LENGTH -> "Password must be 8+ characters";
                case COLON_SYMBOL -> "No ':' allowed";
                default -> "Error";
            });
            if (status == AccountManager.signUpStatus.SUCCESS) {
                new Thread(() -> {
                    try { Thread.sleep(1200); } catch (Exception ignored) {}
                    javafx.application.Platform.runLater(() -> primaryStage.setScene(loginScene));
                }).start();
            }
        });
        signUpBackButton.setOnAction(e -> primaryStage.setScene(loginScene));

        loginScene = new Scene(loginBox, 700, 500);
    }

    private void createMainAppStructure() {
        rootPane = new BorderPane();

        Button musicBtn = new Button("Play Music");
        Button menuBtn = new Button("Main Menu");
        Button signOutBtn = new Button("Sign Out");

        HBox toolbar = new HBox(15, musicBtn, menuBtn, signOutBtn);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        musicBtn.setOnAction(e -> {
            if (mediaPlayer == null) return;
            if (isMusicPlaying) {
                mediaPlayer.pause();
                musicBtn.setText("Play Music");
                isMusicPlaying = false;
            } else {
                mediaPlayer.play();
                musicBtn.setText("Pause Music");
                isMusicPlaying = true;
            }
        });

        menuBtn.setOnAction(e -> showMainMenu());
        signOutBtn.setOnAction(e -> {
            if (mediaPlayer != null && isMusicPlaying) mediaPlayer.stop();
            isMusicPlaying = false;
            musicBtn.setText("Play Music");
            currentUsername = "";
            primaryStage.setScene(loginScene);
        });

        rootPane.setTop(toolbar);
        mainScene = new Scene(rootPane, 1100, 700);
    }

    private void showMainMenu() {
        VBox left = new VBox(15);
        left.setPadding(new Insets(30));
        Label scoreTitle = new Label("Top 5 High Scores");
        scoreTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label bjLabel = new Label("Blackjack:\nLoading...");
        Label snakeLabel = new Label("Snake:\nLoading...");
        left.getChildren().addAll(scoreTitle, bjLabel, snakeLabel);

        VBox center = new VBox(15);
        center.setPadding(new Insets(30));
        Label savesTitle = new Label("Saved Blackjack Games");
        savesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox savesBox = new VBox(8);
        savesBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5;");
        center.getChildren().addAll(savesTitle, savesBox);

        VBox right = new VBox(20);
        right.setPadding(new Insets(40));
        right.setAlignment(Pos.CENTER);
        Label gamesTitle = new Label("Play Games");
        gamesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Button newBj = new Button("New Blackjack Game");
        Button snake = new Button("Snake Game");
        newBj.setPrefSize(200, 50);
        snake.setPrefSize(200, 50);
        newBj.setOnAction(e -> launchBlackjack());
        snake.setOnAction(e -> rootPane.setCenter(new Label("Snake Game\nComing Soon!")));
        right.getChildren().addAll(gamesTitle, newBj, snake);

        HBox content = new HBox(60, left, center, right);
        content.setAlignment(Pos.CENTER);
        rootPane.setCenter(content);

        loadHighScores(bjLabel, snakeLabel);
        loadSavedGames(savesBox);
    }

    private void loadHighScores(Label bjLabel, Label snakeLabel) {
        Path file = Path.of("data/high_scores.txt");
        List<String> bj = new ArrayList<>(), sk = new ArrayList<>();
        try {
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file)) {
                    String[] p = line.split(":", 3);
                    if (p.length == 3) {
                        if ("Blackjack".equals(p[0])) bj.add(p[1] + " - " + p[2]);
                        if ("Snake".equals(p[0])) sk.add(p[1] + " - " + p[2]);
                    }
                }
            }
        } catch (Exception ignored) {}
        if (bj.isEmpty()) bj.addAll(List.of("Player1 - 1000", "Player2 - 950", "Bot - 900", "Guest - 850", "Newbie - 800"));
        if (sk.isEmpty()) sk.addAll(List.of("SnakePro - 5000", "LongBoi - 4200", "Speedy - 3800", "Pro - 3200", "Guest - 2900"));
        bjLabel.setText("Blackjack:\n" + String.join("\n", bj.subList(0, Math.min(5, bj.size()))));
        snakeLabel.setText("Snake:\n" + String.join("\n", sk.subList(0, Math.min(5, sk.size()))));
    }

    private void loadSavedGames(VBox container) {
        container.getChildren().clear();
        Path dir = Path.of("data/saves_blackjack");
        try {
            Files.createDirectories(dir);
            var files = Files.list(dir).filter(p -> p.toString().endsWith(".json")).sorted().toList();
            if (files.isEmpty()) {
                container.getChildren().add(new Label("No saved games.\nClick 'New Blackjack Game' to start!"));
                return;
            }
            for (Path f : files) {
                String name = f.getFileName().toString().replace(".json", "");
                Button btn = new Button("Resume: " + name);
                btn.setPrefWidth(220);
                btn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/blackjack.fxml"));
                        Parent root = loader.load();
                        BlackjackController ctrl = loader.getController();
                        ctrl.init(name);
                        ctrl.onLoad();
                        ctrl.refreshAfterLoad();  // FIXED: buttons now work
                        rootPane.setCenter(root);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                container.getChildren().add(btn);
            }
        } catch (Exception e) {
            container.getChildren().add(new Label("Error loading saves"));
        }
    }

    private void launchBlackjack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/blackjack.fxml"));
            Parent root = loader.load();
            BlackjackController ctrl = loader.getController();
            ctrl.init(currentUsername);
            rootPane.setCenter(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // PUBLIC STATIC METHOD — CALL FROM BLACKJACK
    public static void updateHighScoreIfNeeded(String gameName, String username, int score) {
        Path file = Path.of("data/high_scores.txt");
        String newEntry = gameName + ":" + username + ":" + score;

        try {
            Files.createDirectories(file.getParent());
            List<String> lines = Files.exists(file) ? Files.readAllLines(file) : new ArrayList<>();
            boolean found = false;

            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(":", 3);
                if (parts.length == 3 && parts[0].equals(gameName) && parts[1].equals(username)) {
                    int old = Integer.parseInt(parts[2]);
                    if (score > old) lines.set(i, newEntry);
                    found = true;
                    break;
                }
            }
            if (!found) lines.add(newEntry);

            Map<String, List<String>> grouped = new HashMap<>();
            for (String line : lines) {
                String[] p = line.split(":", 3);
                if (p.length == 3) grouped.computeIfAbsent(p[0], k -> new ArrayList<>()).add(line);
            }
            List<String> cleaned = new ArrayList<>();
            for (List<String> list : grouped.values()) {
                list.sort((a, b) -> Integer.compare(
                    Integer.parseInt(b.split(":")[2]),
                    Integer.parseInt(a.split(":")[2])
                ));
                cleaned.addAll(list.subList(0, Math.min(10, list.size())));
            }
            Files.write(file, cleaned);
        } catch (Exception e) {
            System.out.println("High score save failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}