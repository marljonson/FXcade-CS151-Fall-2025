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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.layout.Priority;
import manager.AccountManager;
import snake.controller.SnakeController;
import ui.BlackjackController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

public class Main extends Application {

    public static final double WINDOW_WIDTH = 700;
    public static final double WINDOW_HEIGHT = 700;

    private AccountManager accountManager;
    private MediaPlayer mediaPlayer; // Field for music
    private boolean isMusicPlaying = false; // Field for music
    private MediaPlayer sfxPlayer;
    private VBox snakeListBox;
    private VBox blackjackListBox;
    private Label welcomeLabel;

    public Main() {
        this.accountManager = new AccountManager();
    }

    @Override
    public void start(Stage primaryStage) {

        // Music setup
        try {
            var url = getClass().getResource("/audio/snake_game.mp3");
            if (url == null) {
                throw new RuntimeException("MP3 file not found in resources!");
            }
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop forever
        } catch (Exception e) {
            System.out.println("Failed to load music: " + e.getMessage());
        }

        try {
            var chimeUrl = getClass().getResource("/audio/chime.mp3");
            if (chimeUrl == null) {
                throw new RuntimeException("MP3 file not found in resources!");
            }
            Media chimeMedia = new Media(chimeUrl.toExternalForm());
            sfxPlayer = new MediaPlayer(chimeMedia);
        } catch (Exception e) {
            System.out.println("Failed to load chime SFX: ");
        }

        // Login scene
        Label welcomeMessage = new Label("Login");
        welcomeMessage.setStyle("-fx-font-weight: bold");
        welcomeMessage.setFont(new Font("System", 24));

        // Username text
        Label usernameLabel = new Label("Username: ");
        usernameLabel.setFont(new Font("System", 15));
        HBox usernameLabelBox = new HBox(usernameLabel);
        usernameLabelBox.setAlignment(Pos.CENTER_LEFT);
        usernameLabelBox.setPrefWidth(200);
        usernameLabelBox.setMaxWidth(Region.USE_PREF_SIZE);

        // Username entry
        TextField loginUsernameField = new TextField();
        loginUsernameField.setPromptText("Username");
        loginUsernameField.setPrefWidth(200);
        loginUsernameField.setMaxWidth(Region.USE_PREF_SIZE);

        // Password text
        Label passwordLabel = new Label("Password: ");
        passwordLabel.setFont(new Font("System", 15));
        HBox passwordLabelBox = new HBox(passwordLabel);
        passwordLabelBox.setAlignment(Pos.CENTER_LEFT);
        passwordLabelBox.setPrefWidth(200);
        passwordLabelBox.setMaxWidth(Region.USE_PREF_SIZE);

        // Password entry
        TextField loginPasswordField = new TextField();
        loginPasswordField.setPromptText("Password");
        loginPasswordField.setPrefWidth(200);
        loginPasswordField.setMaxWidth(Region.USE_PREF_SIZE);

        Label loginMessageLabel = new Label();
        loginMessageLabel.setTextFill(Color.RED);

        // Sign in button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(100);
        loginButton.setPrefHeight(30);
        loginButton.setFont(new Font("System", 15));
        String loginNormal = "-fx-background-color: linear-gradient(#4aa3ff, #1e88e5);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #0b63c7;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String loginHover = "-fx-background-color: linear-gradient(#82c4ff, #4aa3ff);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #0b63c7;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        loginButton.setStyle(loginNormal);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(loginHover));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(loginNormal));

        Label createAccountMessage = new Label("Don't have an account yet?");

        // Sign up button
        Button createAccountButton = new Button("Sign Up");
        createAccountButton.setPrefWidth(100);
        createAccountButton.setPrefHeight(30);
        createAccountButton.setFont(new Font("System", 15));

        String createAccountNormal = "-fx-background-color: linear-gradient(#ffffff, #f2f2f2);" +
                "-fx-text-fill: #1e88e5;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1e88e5;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String createAccountHover = "-fx-background-color: linear-gradient(#e8f3ff, #d6eaff);" +
                "-fx-text-fill: #1565c0;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1565c0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        createAccountButton.setStyle(createAccountNormal);
        createAccountButton.setOnMouseEntered(e -> createAccountButton.setStyle(createAccountHover));
        createAccountButton.setOnMouseExited(e -> createAccountButton.setStyle(createAccountNormal));

        VBox signInlayout = new VBox(welcomeMessage, usernameLabelBox, loginUsernameField, passwordLabelBox,
                loginPasswordField, loginButton, loginMessageLabel, createAccountMessage, createAccountButton);
        signInlayout.setSpacing(10);
        signInlayout.setPadding(new Insets(30));
        signInlayout.setAlignment(Pos.CENTER);
        Scene loginScene = new Scene(signInlayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("FXcade Game Manager");
        primaryStage.show();

        // Sign up scene
        Label signUpMessage = new Label("Sign Up");
        signUpMessage.setStyle("-fx-font-weight: bold");
        signUpMessage.setFont(new Font("System", 24));

        // Username text
        Label usernameSignUpLabel = new Label("Username: ");
        usernameSignUpLabel.setFont(new Font("System", 15));
        HBox usernameSignUpLabelBox = new HBox(usernameSignUpLabel);
        usernameSignUpLabelBox.setAlignment(Pos.CENTER_LEFT);
        usernameSignUpLabelBox.setPrefWidth(200);
        usernameSignUpLabelBox.setMaxWidth(Region.USE_PREF_SIZE);

        // Username entry
        TextField signupUsernameField = new TextField();
        signupUsernameField.setPromptText("Username");
        signupUsernameField.setPrefWidth(200);
        signupUsernameField.setMaxWidth(Region.USE_PREF_SIZE);

        // Password text
        Label passwordSignUpLabel = new Label("Password: ");
        passwordSignUpLabel.setFont(new Font("System", 15));
        HBox passwordSignUpLabelBox = new HBox(passwordSignUpLabel);
        passwordSignUpLabelBox.setAlignment(Pos.CENTER_LEFT);
        passwordSignUpLabelBox.setPrefWidth(200);
        passwordSignUpLabelBox.setMaxWidth(Region.USE_PREF_SIZE);

        // Password entry
        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");
        signupPasswordField.setPrefWidth(200);
        signupPasswordField.setMaxWidth(Region.USE_PREF_SIZE);

        Button signUpButton = new Button("Sign Up");
        signUpButton.setPrefWidth(100);
        signUpButton.setPrefHeight(30);
        signUpButton.setFont(new Font("System", 15));

        String signUpNormal = "-fx-background-color: linear-gradient(#ffffff, #f2f2f2);" +
                "-fx-text-fill: #1e88e5;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1e88e5;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String signUpHover = "-fx-background-color: linear-gradient(#e8f3ff, #d6eaff);" +
                "-fx-text-fill: #1565c0;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1565c0;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        signUpButton.setStyle(signUpNormal);
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle(signUpHover));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle(signUpNormal));

        Label signUpMessageLabel = new Label();
        signUpMessageLabel.setTextFill(Color.RED);

        Button signUpBackButton = new Button("Back");
        signUpBackButton.setPrefWidth(70);
        signUpBackButton.setPrefHeight(20);
        signUpBackButton.setFont(new Font("System", 13));

        String signUpBackNormal = "-fx-background-color: #555555;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String signUpBackHover = "-fx-background-color: #666666;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        signUpBackButton.setStyle(signUpBackNormal);
        signUpBackButton.setOnMouseEntered(e -> signUpBackButton.setStyle(signUpBackHover));
        signUpBackButton.setOnMouseExited(e -> signUpBackButton.setStyle(signUpBackNormal));

        VBox signupLayout = new VBox(signUpMessage, usernameSignUpLabelBox, signupUsernameField, passwordSignUpLabelBox,
                signupPasswordField, signUpButton, signUpMessageLabel, signUpBackButton);
        signupLayout.setSpacing(10);
        signupLayout.setPadding(new Insets(30));
        signupLayout.setAlignment(Pos.CENTER);
        Scene signupScene = new Scene(signupLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Main menu scene
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color:  #f2f2f7;");

        // Left main menu
        Label topScore = new Label("Top Scores");
        topScore.setStyle("-fx-font-weight: bold");
        topScore.setFont(new Font("System", 24));

        Line underline = new Line(0, 0, 140, 0);
        underline.setStroke(Color.rgb(0, 0, 0, 0.15));
        underline.setStrokeWidth(2);

        Label blackjackScores = new Label("Blackjack");
        blackjackScores.setFont(new Font("System", 18));
        blackjackScores.setStyle("-fx-text-fill: #1A3D7C; -fx-font-weight: bold;");

        Label snakeScores = new Label("Snake");
        snakeScores.setFont(new Font("System", 18));
        snakeScores.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");

        blackjackListBox = new VBox();
        blackjackListBox.setSpacing(4);

        snakeListBox = new VBox();
        snakeListBox.setSpacing(4);

        VBox mainMenuLeft = new VBox(topScore, underline, blackjackScores, blackjackListBox, snakeScores, snakeListBox);
        mainMenuLeft.setSpacing(10);
        mainMenuLeft.setPadding(new Insets(20));

        // Right main menu
        Label gameMenu = new Label("Play Games");
        gameMenu.setStyle("-fx-font-weight: bold");
        gameMenu.setFont(new Font("System", 24));

        Button blackjackButton = new Button("Blackjack");
        blackjackButton.setStyle("-fx-background-radius: 8;");
        blackjackButton.setPrefWidth(115);
        blackjackButton.setPrefHeight(30);
        blackjackButton.setFont(new Font("System", 16));

        String blackjackNormal = "-fx-background-color: #1E88E5;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #0D47A1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        String blackjackHover = "-fx-background-color: #2196F3;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #0D47A1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        blackjackButton.setStyle(blackjackNormal);
        blackjackButton.setOnMouseEntered(e -> blackjackButton.setStyle(blackjackHover));
        blackjackButton.setOnMouseExited(e -> blackjackButton.setStyle(blackjackNormal));

        Button snakeButton = new Button("Snake");
        snakeButton.setStyle("-fx-background-radius: 8;");
        snakeButton.setPrefWidth(100);
        snakeButton.setPrefHeight(30);
        snakeButton.setFont(new Font("System", 16));

        String snakeNormal = "-fx-background-color: #2E7D32;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1B5E20;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String snakeHover = "-fx-background-color: #388E3C;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #1B5E20;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        snakeButton.setStyle(snakeNormal);
        snakeButton.setOnMouseEntered(e -> snakeButton.setStyle(snakeHover));
        snakeButton.setOnMouseExited(e -> snakeButton.setStyle(snakeNormal));

        Label addGameMenu = new Label("Add Games");
        addGameMenu.setStyle("-fx-font-weight: bold");
        addGameMenu.setFont(new Font("System", 24));

        Button addGameButton1 = new Button("Add Game");
        addGameButton1.setStyle("-fx-background-radius: 8;");
        addGameButton1.setPrefWidth(130);
        addGameButton1.setPrefHeight(30);
        addGameButton1.setFont(new Font("System", 16));

        String addGameButton1Normal = "-fx-background-color: #555555;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String addGameButton1Hover = "-fx-background-color: #666666;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        addGameButton1.setStyle(addGameButton1Normal);
        addGameButton1.setOnMouseEntered(e -> addGameButton1.setStyle(addGameButton1Hover));
        addGameButton1.setOnMouseExited(e -> addGameButton1.setStyle(addGameButton1Normal));

        Button addGameButton2 = new Button("Add Game");
        addGameButton2.setStyle("-fx-background-radius: 8;");
        addGameButton2.setPrefWidth(130);
        addGameButton2.setPrefHeight(30);
        addGameButton2.setFont(new Font("System", 16));

        String addGameButton2Normal = "-fx-background-color: #555555;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";
        String addGameButton2Hover = "-fx-background-color: #666666;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 6 18;";

        addGameButton2.setStyle(addGameButton2Normal);
        addGameButton2.setOnMouseEntered(e -> addGameButton2.setStyle(addGameButton2Hover));
        addGameButton2.setOnMouseExited(e -> addGameButton2.setStyle(addGameButton2Normal));

        VBox mainMenuRight = new VBox(gameMenu, blackjackButton, snakeButton, addGameMenu, addGameButton1,
                addGameButton2);
        mainMenuRight.setSpacing(10);
        mainMenuRight.setPadding(new Insets(20));

        HBox mainMenu = new HBox(mainMenuLeft, mainMenuRight);
        mainMenu.setSpacing(60);
        mainMenu.setPadding(new Insets(40));
        mainMenu.setAlignment(Pos.TOP_CENTER);

        borderPane.setCenter(mainMenu);

        Scene mainMenuScene = new Scene(borderPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        HBox toolBar = createToolBar(primaryStage, loginScene, mainMenuScene);

        welcomeLabel = new Label();
        welcomeLabel.setStyle("-fx-font-style: italic; -fx-font-size: 16px;");
        welcomeLabel.setPadding(new Insets(10, 0, 10, 20));

        VBox topArea = new VBox(toolBar, welcomeLabel);
        borderPane.setTop(topArea);

        // Button actions
        // 1 - Login Scene: Sign in button clicked
        loginButton.setOnAction(event -> {
            String loginUsername = loginUsernameField.getText();
            String loginPassword = loginPasswordField.getText();

            if (loginUsername.isEmpty() || loginPassword.isEmpty()) {
                loginMessageLabel.setText("Please enter both username and password.");
            } else {
                AccountManager.loginStatus signInStatus = accountManager.signIn(loginUsername, loginPassword);
                switch (signInStatus) {
                    case SUCCESS:
                        // Change scene to main menu
                        if (sfxPlayer != null) {
                            sfxPlayer.stop();
                            sfxPlayer.play();
                        }
                        updateBlackjackTopScores(accountManager, blackjackListBox);
                        updateSnakeTopScores(accountManager, snakeListBox);
                        welcomeLabel
                                .setText("Welcome to FXcade, " + accountManager.getActiveUser().getUsername() + "!");
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

        // 2 - Login Scene: Sign up button clicked
        createAccountButton.setOnAction(e -> {
            primaryStage.setScene(signupScene);
        });

        // 3 - Sign up scene: Sign up button clicked
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

        // 4 - Sign Up scene: Back button clicked
        signUpBackButton.setOnAction(e -> {
            primaryStage.setScene(loginScene);
        });

        // Launch blackjack game
        blackjackButton.setOnAction(e -> {
            new BlackjackController(primaryStage, () -> {
                primaryStage.setScene(mainMenuScene);
                primaryStage.setTitle("FXcade Game Manager");
                // updateTopScores(accountManager, snakeListBox, blackjackListBox);
                updateBlackjackTopScores(accountManager, blackjackListBox);
                // Restart main menu music when returning
                if (mediaPlayer != null) {
                    mediaPlayer.play();
                    isMusicPlaying = true;
                }
            }).start(accountManager.getActiveUser().getUsername());

            // Stop main menu music when entering Blackjack
            if (mediaPlayer != null && isMusicPlaying) {
                mediaPlayer.pause();
                isMusicPlaying = false;
            }
        });

        // 7 - Launch snake game
        snakeButton.setOnAction(e -> {
            SnakeController controller = new SnakeController(primaryStage, accountManager.getActiveUser().getUsername(),
                    () -> {
                        // Snake Main Menu button
                        primaryStage.setScene(mainMenuScene);
                        primaryStage.setTitle("FXcade Game Manager");
                        mainMenu.requestFocus();
                    });

            updateSnakeTopScores(accountManager, snakeListBox);

            HBox snakeToolBar = createToolBar(primaryStage, loginScene, mainMenuScene);
            controller.getView().setToolbar(snakeToolBar);

            primaryStage.setScene(controller.getView().getScene());
            primaryStage.setTitle("Snake Game");
            // primaryStage.getScene().getRoot().requestFocus();
            controller.getView().getCanvas().requestFocus();
        });
    }

    private HBox createToolBar(Stage primaryStage, Scene loginScene, Scene mainMenuScene) {
        // Top toolbar
        Button musicToggleButton = new Button("Play Music"); // Music toggle button
        musicToggleButton.setFont(new Font("System", 13));
        musicToggleButton.setStyle("-fx-background-radius: 8;");

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setFont(new Font("System", 13));
        mainMenuButton.setStyle("-fx-background-radius: 8;");

        Button signOutButton = new Button("Sign Out");
        signOutButton.setFont(new Font("System", 13));
        signOutButton.setStyle("-fx-background-radius: 8;");

        HBox toolBar = new HBox(musicToggleButton, mainMenuButton, signOutButton);
        toolBar.setSpacing(10);
        toolBar.setPadding(new Insets(10));
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        toolBar.setStyle("-fx-background-color: #555555;");

        // Music toggle action
        musicToggleButton.setOnAction(e -> {
            if (mediaPlayer == null)
                return;
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

        // Tool bar "Main Menu" button
        mainMenuButton.setOnAction(e -> {
            updateBlackjackTopScores(accountManager, blackjackListBox);
            updateSnakeTopScores(accountManager, snakeListBox);
            primaryStage.setScene(mainMenuScene);
            primaryStage.setTitle("FXcade Game Manager");
        });

        // Tool bar "Sign Out" button
        signOutButton.setOnAction(e -> {
            // Stop music if it's playing
            if (mediaPlayer != null && isMusicPlaying) {
                mediaPlayer.stop();
                isMusicPlaying = false;
                musicToggleButton.setText("Play Music");
            }
            // Go back to login scene
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("FXcade Game Manager");
        });

        return toolBar;
    }

    private void updateSnakeTopScores(AccountManager accountManager, VBox snakeListBox) {
        snakeListBox.getChildren().clear();

        String username = accountManager.getActiveUser().getUsername();
        Path filePath = Paths.get("data/high_scores.txt");

        try {
            List<String> lines = Files.readAllLines(filePath);
            String start = username + ":snake:";

            List<Integer> scores = new ArrayList<>();

            // Read snake top scores
            for (String line : lines) {
                if (line.startsWith(start)) {
                    String[] parts = line.split(":");
                    for (int i = 2; i < parts.length; i++) {
                        scores.add(Integer.parseInt(parts[i]));
                    }
                    break;
                }
            }

            // Display snake top scores
            for (int i = 0; i < scores.size(); i++) {
                String text = (i + 1) + ". " + scores.get(i);

                Label scoreLabel = new Label(text);
                scoreLabel.setFont(Font.font("Consolas", 14));
                scoreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1B5E20;");

                HBox row = new HBox(scoreLabel);
                row.setSpacing(8);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(4, 10, 4, 10));

                String backgroundColor = (i % 2 == 0) ? "#C8E6C9" : "#A5D6A7";
                row.setStyle(
                        "-fx-background-color: " + backgroundColor + ";" +
                                "-fx-background-radius: 6;");

                row.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(scoreLabel, Priority.ALWAYS);
                VBox.setMargin(row, new Insets(2, 0, 2, 0));

                snakeListBox.getChildren().add(row);
            }
        } catch (IOException e) {

        }
    }

    private void updateBlackjackTopScores(AccountManager accountManager, VBox blackjackListBox) {
        blackjackListBox.getChildren().clear();

        String username = accountManager.getActiveUser().getUsername();
        Path filePath = Paths.get("data/blackjack_high_scores.txt");

        Integer best = null;

        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                String prefix = username + ":";

                // Find this user's line
                for (String line : lines) {
                    if (line.startsWith(prefix)) {
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            best = Integer.parseInt(parts[1]);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading blackjack high scores: " + e.getMessage());
        }

        // Always build 5 entries
        List<Integer> scores = new ArrayList<>();
        if (best != null) {
            scores.add(best);
        }
        while (scores.size() < 5) {
            scores.add(0);
        }

        for (int i = 0; i < scores.size(); i++) {
            String text = (i + 1) + ". " + scores.get(i);

            Label scoreLabel = new Label(text);
            scoreLabel.setFont(Font.font("Consolas", 14));
            scoreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1B5E20;");

            HBox row = new HBox(scoreLabel);
            row.setSpacing(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 10, 4, 10));

            String backgroundColor = (i % 2 == 0) ? "#C8E6C9" : "#A5D6A7";
            row.setStyle(
                    "-fx-background-color: " + backgroundColor + ";" +
                            "-fx-background-radius: 6;");

            row.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(scoreLabel, Priority.ALWAYS);
            VBox.setMargin(row, new Insets(2, 0, 2, 0));

            blackjackListBox.getChildren().add(row);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}