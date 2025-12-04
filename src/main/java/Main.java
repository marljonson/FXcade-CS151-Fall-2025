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
import manager.AccountManager;
import snake.controller.SnakeController;

public class Main extends Application {

    public static final double WINDOW_WIDTH = 700;
    public static final double WINDOW_HEIGHT = 700;

    private AccountManager accountManager;
    private MediaPlayer mediaPlayer; // Field for music
    private boolean isMusicPlaying = false; // Field for music
    private MediaPlayer sfxPlayer;

    public Main() {
        this.accountManager = new AccountManager();
    }

    @Override
    public void start(Stage primaryStage) {

        // Music setup
        try {
            var url = getClass().getResource("/audio/catherine.mp3");
            if (url == null) {
                throw new RuntimeException("MP3 file not found in resources!");
            }
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // Loop forever
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


        VBox signInlayout = new VBox(welcomeMessage, usernameLabelBox, loginUsernameField, passwordLabelBox, loginPasswordField, loginButton, loginMessageLabel, createAccountMessage, createAccountButton);
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

        VBox signupLayout = new VBox(signUpMessage, usernameSignUpLabelBox, signupUsernameField, passwordSignUpLabelBox, signupPasswordField, signUpButton, signUpMessageLabel, signUpBackButton);
        signupLayout.setSpacing(10);
        signupLayout.setPadding(new Insets(30));
        signupLayout.setAlignment(Pos.CENTER);
        Scene signupScene = new Scene(signupLayout, WINDOW_WIDTH, WINDOW_HEIGHT);


        // Main menu scene
        BorderPane borderPane = new BorderPane();

        // Left main menu
        Label topScore = new Label("Top Scores");
        topScore.setStyle("-fx-font-weight: bold");
        topScore.setFont(new Font("System", 24));

        Label blackjackScores = new Label("Blackjack:");
        Label snakeScores = new Label("Snake");
        topScore.setFont(new Font("System", 18));

        VBox snakeListBox = new VBox();

        VBox mainMenuLeft = new VBox(topScore, blackjackScores, snakeScores, snakeListBox);
        mainMenuLeft.setSpacing(10);
        mainMenuLeft.setPadding(new Insets(20));

        // Right main menu
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
        addGameButton1.setStyle("-fx-background-radius: 8;");
        addGameButton1.setPrefWidth(120);
        addGameButton1.setPrefHeight(30);
        addGameButton1.setFont(new Font("System", 16));

        Button addGameButton2 = new Button("Add Game");
        addGameButton2.setStyle("-fx-background-radius: 8;");
        addGameButton2.setPrefWidth(120);
        addGameButton2.setPrefHeight(30);
        addGameButton2.setFont(new Font("System", 16));

        VBox mainMenuRight = new VBox(gameMenu, blackjackButton, snakeButton, addGameMenu, addGameButton1, addGameButton2);
        mainMenuRight.setSpacing(10);
        mainMenuRight.setPadding(new Insets(20));

        HBox mainMenu = new HBox(mainMenuLeft, mainMenuRight);
        mainMenu.setSpacing(60);
        mainMenu.setPadding(new Insets(40));
        mainMenu.setAlignment(Pos.TOP_CENTER);

        borderPane.setCenter(mainMenu);

        Scene mainMenuScene = new Scene(borderPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        HBox toolBar = createToolBar(primaryStage, loginScene, mainMenuScene);
        borderPane.setTop(toolBar);

        // updateSnakeTopScores(accountManager, VBox snakeListBox);

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

        // 7 - Launch snake game
        snakeButton.setOnAction(e -> {
            SnakeController controller = new SnakeController(primaryStage, accountManager.getActiveUser().getUsername(), () -> {
                // Snake Main Menu button
                primaryStage.setScene(mainMenuScene);
                primaryStage.setTitle("FXcade Game Manager");
                mainMenu.requestFocus();
            });

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

        // Tool bar "Main Menu" button
        mainMenuButton.setOnAction(e -> {
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

    public static void main(String[] args) {
        launch(args);
    }
}
