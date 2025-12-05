import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import manager.AccountManager;
import snake.controller.SnakeController;
import ui.BlackjackController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {

    private AccountManager accountManager;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;
    private MediaPlayer sfxPlayer;
    private VBox snakeListBox;
    private Label welcomeLabel;
    private Scene mainMenuScene;

    public Main() {
        this.accountManager = new AccountManager();
    }

    @Override
    public void start(Stage primaryStage) {

        // Music setup
        try {
            var url = getClass().getResource("/audio/lamb_game.mp3");
            if (url == null) {
                throw new RuntimeException("MP3 file not found in resources!");
            }
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            System.out.println("Failed to load music: " + e.getMessage());
        }

        try {
            var popUrl = getClass().getResource("/audio/chime.mp3");
            if (popUrl == null) {
                throw new RuntimeException("MP3 file not found in resources!");
            }
            Media popMedia = new Media(popUrl.toExternalForm());
            sfxPlayer = new MediaPlayer(popMedia);
        } catch (Exception e) {
            System.out.println("Failed to load pop SFX: ");
        }

        // Login scene
        Label welcomeMessage = new Label("Login");
        welcomeMessage.setStyle("-fx-font-weight: bold");
        welcomeMessage.setFont(new Font("System", 24));

                Label usernameLabel = new Label("Username: ");
        usernameLabel.setFont(new Font("System", 15));
        usernameLabel.setPrefWidth(100);
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);

        TextField loginUsernameField = new TextField();
        loginUsernameField.setPromptText("Enter username");
        loginUsernameField.setPrefWidth(200);

        HBox usernameRow = new HBox(15, usernameLabel, loginUsernameField);
        usernameRow.setAlignment(Pos.CENTER);

        Label passwordLabel = new Label("Password: ");
        passwordLabel.setFont(new Font("System", 15));
        passwordLabel.setPrefWidth(100);
        passwordLabel.setAlignment(Pos.CENTER_RIGHT);

        TextField loginPasswordField = new TextField();
        loginPasswordField.setPromptText("Enter password");
        loginPasswordField.setPrefWidth(200);

        HBox passwordRow = new HBox(15, passwordLabel, loginPasswordField);
        passwordRow.setAlignment(Pos.CENTER);

        Label loginMessageLabel = new Label();
        loginMessageLabel.setTextFill(Color.RED);

        Button loginButton = createStyledButton("Sign In", "#4aa3ff", "#1e88e5");
        Button createAccountButton = createStyledButton("Sign Up", "#ffffff", "#f2f2f2", "#1e88e5");

        VBox signInlayout = new VBox(20,
            welcomeMessage,
            usernameRow,
            passwordRow,
            loginButton,
            loginMessageLabel,
            createAccountButton
        );
        signInlayout.setSpacing(10);
        signInlayout.setPadding(new Insets(30));
        signInlayout.setAlignment(Pos.CENTER);
        Scene loginScene = new Scene(signInlayout, 700, 700);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("FXcade Game Manager");
        primaryStage.show();

        // Sign up scene
        Label signUpMessage = new Label("Sign Up");
        signUpMessage.setStyle("-fx-font-weight: bold");
        signUpMessage.setFont(new Font("System", 24));

        TextField signupUsernameField = new TextField();
        signupUsernameField.setPromptText("Username");
        signupUsernameField.setPrefWidth(200);

        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");
        signupPasswordField.setPrefWidth(200);

        Button signUpButton = createStyledButton("Sign Up", "#ffffff", "#f2f2f2", "#1e88e5");
        Label signUpMessageLabel = new Label();
        signUpMessageLabel.setTextFill(Color.RED);
        Button signUpBackButton = new Button("Back");

        VBox signupLayout = new VBox(signUpMessage, new Label("Username:"), signupUsernameField,
                new Label("Password:"), signupPasswordField, signUpButton, signUpMessageLabel, signUpBackButton);
        signupLayout.setSpacing(10);
        signupLayout.setPadding(new Insets(30));
        signupLayout.setAlignment(Pos.CENTER);
        Scene signupScene = new Scene(signupLayout, 700, 700);

        // Main menu scene
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #f2f2f7;");

        Label topScore = new Label("Top Scores");
        topScore.setStyle("-fx-font-weight: bold");
        topScore.setFont(new Font("System", 24));

        Line underline = new Line(0, 0, 140, 0);
        underline.setStroke(Color.rgb(0, 0, 0, 0.15));
        underline.setStrokeWidth(2);

        Label blackjackScores = new Label("Blackjack");
        blackjackScores.setFont(new Font("System", 18));
        blackjackScores.setStyle("-fx-text-fill: #1A3D7C; -fx-font-weight: bold;");

        VBox blackjackListBox = new VBox();
        blackjackListBox.setSpacing(4);

        Label snakeScores = new Label("Snake");
        snakeScores.setFont(new Font("System", 18));
        snakeScores.setStyle("-fx-text-fill: #1A3D7C; -fx-font-weight: bold;");

        snakeListBox = new VBox();
        snakeListBox.setSpacing(4);

        VBox mainMenuLeft = new VBox(topScore, underline, 
            blackjackScores, blackjackListBox, 
            snakeScores, snakeListBox);
        mainMenuLeft.setSpacing(10);
        mainMenuLeft.setPadding(new Insets(20));

        Label gameMenu = new Label("Play Games");
        gameMenu.setStyle("-fx-font-weight: bold");
        gameMenu.setFont(new Font("System", 24));

        Button blackjackButton = new Button("Blackjack");
        blackjackButton.setStyle("-fx-background-radius: 8;");
        blackjackButton.setPrefWidth(100);
        blackjackButton.setPrefHeight(30);
        blackjackButton.setFont(new Font("System", 16));

        Button snakeButton = new Button("Snake");
        snakeButton.setStyle("-fx-background-radius: 8;");
        snakeButton.setPrefWidth(100);
        snakeButton.setPrefHeight(30);
        snakeButton.setFont(new Font("System", 16));

        Label addGameMenu = new Label("Add Games");
        addGameMenu.setStyle("-fx-font-weight: bold");
        addGameMenu.setFont(new Font("System", 24));

        Button addGameButton1 = new Button("Add Game");
        Button addGameButton2 = new Button("Add Game");

        VBox mainMenuRight = new VBox(gameMenu, blackjackButton, snakeButton, addGameMenu, addGameButton1, addGameButton2);
        mainMenuRight.setSpacing(10);
        mainMenuRight.setPadding(new Insets(20));

        HBox mainMenu = new HBox(mainMenuLeft, mainMenuRight);
        mainMenu.setSpacing(60);
        mainMenu.setPadding(new Insets(40));
        mainMenu.setAlignment(Pos.TOP_CENTER);

        borderPane.setCenter(mainMenu);
        mainMenuScene = new Scene(borderPane, 700, 700);

        HBox toolBar = createToolBar(primaryStage, loginScene, mainMenuScene);

        welcomeLabel = new Label();
        welcomeLabel.setStyle("-fx-font-style: italic; -fx-font-size: 16px;");
        welcomeLabel.setPadding(new Insets(10, 0, 10, 20));

        VBox topArea = new VBox(toolBar, welcomeLabel);
        borderPane.setTop(topArea);

        // ==================== BUTTON ACTIONS ====================

        loginButton.setOnAction(event -> {
            String loginUsername = loginUsernameField.getText();
            String loginPassword = loginPasswordField.getText();

            if (loginUsername.isEmpty() || loginPassword.isEmpty()) {
                loginMessageLabel.setText("Please enter both username and password.");
            } else {
                AccountManager.loginStatus signInStatus = accountManager.signIn(loginUsername, loginPassword);
                switch (signInStatus) {
                    case SUCCESS:
                        if (sfxPlayer != null) {
                            sfxPlayer.stop();
                            sfxPlayer.play();
                        }
                        updateTopScores(accountManager, snakeListBox, blackjackListBox);
                        welcomeLabel.setText("Welcome to FXcade, " + accountManager.getActiveUser().getUsername() + "!");
                        primaryStage.setScene(mainMenuScene);
                        break;
                    case USER_NOT_FOUND:
                        loginMessageLabel.setText("User not found. Please try again.");
                        break;
                    case WRONG_PASSWORD:
                        loginMessageLabel.setText("Wrong password. Please try again.");
                        break;
                }
            }
        });

        createAccountButton.setOnAction(e -> primaryStage.setScene(signupScene));

        signUpButton.setOnAction(e -> {
            String signupUsername = signupUsernameField.getText();
            String signupPassword = signupPasswordField.getText();

            if (signupUsername.isEmpty() || signupPassword.isEmpty()) {
                signUpMessageLabel.setText("Please enter both username and password.");
            } else {
                AccountManager.signUpStatus signUpStatus = accountManager.createUser(signupUsername, signupPassword);
                switch (signUpStatus) {
                    case SUCCESS:
                        signUpMessageLabel.setTextFill(Color.GREEN);
                        signUpMessageLabel.setText("Sign Up Successful! Please sign in.");
                        break;
                    case USER_TAKEN:
                        signUpMessageLabel.setTextFill(Color.RED);
                        signUpMessageLabel.setText("Username taken. Please try again.");
                        break;
                    case PASSWORD_LENGTH:
                        signUpMessageLabel.setTextFill(Color.RED);
                        signUpMessageLabel.setText("Password length must be longer than 8. Please try again.");
                        break;
                    case COLON_SYMBOL:
                        signUpMessageLabel.setTextFill(Color.RED);
                        signUpMessageLabel.setText("Username and password cannot contain colon. Please try again.");
                        break;
                    case FILE_ERROR:
                        signUpMessageLabel.setTextFill(Color.RED);
                        signUpMessageLabel.setText("File error. Please try again.");
                        break;
                }
            }
        });

        signUpBackButton.setOnAction(e -> primaryStage.setScene(loginScene));

        // Snake Button
        snakeButton.setOnAction(e -> {
            SnakeController controller = new SnakeController(primaryStage, accountManager.getActiveUser().getUsername(), () -> {
                primaryStage.setScene(mainMenuScene);
                primaryStage.setTitle("FXcade Game Manager");
                mainMenu.requestFocus();
            });

            // updateSnakeTopScores(accountManager, snakeListBox);

            HBox snakeToolBar = createToolBar(primaryStage, loginScene, mainMenuScene);
            controller.getView().setToolbar(snakeToolBar);

            primaryStage.setScene(controller.getView().getScene());
            primaryStage.setTitle("Snake Game");
            controller.getView().getCanvas().requestFocus();
        });

        blackjackButton.setOnAction(e -> {
            new BlackjackController(primaryStage, () -> {
                primaryStage.setScene(mainMenuScene);
                primaryStage.setTitle("FXcade Game Manager");
                // update high scores when coming back
                updateTopScores(accountManager, snakeListBox, blackjackListBox);
            }).start(accountManager.getActiveUser().getUsername());
        });

        primaryStage.setScene(loginScene);
    }

    private Button createStyledButton(String text, String bgStart, String bgEnd) {
        return createStyledButton(text, bgStart, bgEnd, null);
    }

    private Button createStyledButton(String text, String bgStart, String bgEnd, String borderColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(100);
        btn.setPrefHeight(30);
        btn.setFont(new Font("System", 15));

        String style = "-fx-background-color: linear-gradient(" + bgStart + ", " + bgEnd + ");" +
                "-fx-text-fill: " + (borderColor == null ? "white" : "#1e88e5") + ";" +
                "-fx-background-radius: 8;" +
                (borderColor != null ? "-fx-border-color: " + borderColor + "; -fx-border-width: 1; -fx-border-radius: 8;" : "") +
                "-fx-padding: 6 18;";

        String hover = style.replace(bgStart, "#" + Integer.toHexString(Integer.parseInt(bgStart.substring(1), 16) + 0x181818));
        btn.setStyle(style);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(style));
        return btn;
    }

    private HBox createToolBar(Stage primaryStage, Scene loginScene, Scene mainMenuScene) {
        Button musicToggleButton = new Button("Play Music");
        Button mainMenuButton = new Button("Main Menu");
        Button signOutButton = new Button("Sign Out");

        HBox toolBar = new HBox(musicToggleButton, mainMenuButton, signOutButton);
        toolBar.setSpacing(10);
        toolBar.setPadding(new Insets(10));
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        toolBar.setStyle("-fx-background-color: #555555;");

        musicToggleButton.setOnAction(e -> {
            if (mediaPlayer == null) return;
            if (isMusicPlaying) {
                mediaPlayer.pause();
                musicToggleButton.setText("Play Music");
                isMusicPlaying = false;
            } else {
                mediaPlayer.play();
                musicToggleButton.setText("Pause Music");
                isMusicPlaying = true;
            }
        });

        mainMenuButton.setOnAction(e -> primaryStage.setScene(mainMenuScene));
        signOutButton.setOnAction(e -> {
            if (mediaPlayer != null && isMusicPlaying) mediaPlayer.stop();
            primaryStage.setScene(loginScene);
        });

        return toolBar;
    }

    private void updateTopScores(AccountManager accountManager, VBox snakeBox, VBox blackjackBox) {
        snakeBox.getChildren().clear();
        blackjackBox.getChildren().clear();

        Path filePath = Paths.get("data/high_scores.txt");
        if (!Files.exists(filePath)) return;

        try {
            List<String> lines = Files.readAllLines(filePath);
            Map<String, List<Integer>> snakeScores = new HashMap<>();
            Map<String, List<Integer>> bjScores = new HashMap<>();

            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length < 3) continue;
                String user = parts[0];
                String game = parts[1];
                List<Integer> scores = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    try { scores.add(Integer.parseInt(parts[i])); } catch (Exception ignored) {}
                }
                if ("snake".equalsIgnoreCase(game)) snakeScores.put(user, scores);
                else if ("blackjack".equalsIgnoreCase(game)) bjScores.put(user, scores);
            }

            displayTop5(snakeScores, snakeBox);
            displayTop5(bjScores, blackjackBox);

        } catch (IOException e) {
            snakeBox.getChildren().add(new Label("Error loading scores"));
        }
    }

    // helper method
    private void displayTop5(Map<String, List<Integer>> map, VBox box) {
        List<Map.Entry<String, Integer>> all = new ArrayList<>();
        for (var e : map.entrySet()) {
            for (int s : e.getValue()) {
                if (s > 0) all.add(Map.entry(e.getKey(), s));
            }
        }
        all.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        int count = 0;
        for (var e : all) {
            if (count >= 5) break;
            box.getChildren().add(new Label((count + 1) + ". " + e.getKey() + " – " + e.getValue()));
            count++;
        }
        if (count == 0) box.getChildren().add(new Label("—"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}