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
import manager.AccountManager;

public class Main extends Application {

    private AccountManager accountManager;

    public Main() {
        this.accountManager = new AccountManager();
    }

    @Override
    public void start(Stage primaryStage) {
        // Login scene
        Label welcomeMessage = new Label("Welcome to FxCade!");
        welcomeMessage.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");

        // Username text
        Label usernameLabel = new Label("Username: ");
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

        Label createAccountMessage = new Label("Don't have an account yet?");

        // Sign up button
        Button createAccountButton = new Button("Sign Up");

        VBox signInlayout = new VBox(welcomeMessage, usernameLabelBox, loginUsernameField, passwordLabelBox, loginPasswordField, loginButton, loginMessageLabel, createAccountMessage, createAccountButton);
        signInlayout.setSpacing(10);
        signInlayout.setPadding(new Insets(30));
        signInlayout.setAlignment(Pos.CENTER);
        Scene loginScene = new Scene(signInlayout, 700, 500);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("FXCade Game Manager");
        primaryStage.show();


        // Sign up scene
        Label signUpMessage = new Label("Please fill in all the fields to sign up.");
        signUpMessage.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");

        // Username text
        Label usernameSignUpLabel = new Label("Username: ");
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
        HBox passwordSignUpLabelBox = new HBox(passwordSignUpLabel);
        passwordSignUpLabelBox.setAlignment(Pos.CENTER_LEFT);
        passwordSignUpLabelBox.setPrefWidth(200);
        passwordSignUpLabelBox.setMaxWidth(Region.USE_PREF_SIZE);

        // Password entry
        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");
        signupPasswordField.setPrefWidth(200);
        signupPasswordField.setMaxWidth(Region.USE_PREF_SIZE);

        Button signUpButton = new Button("Sign up");

        Label signUpMessageLabel = new Label();
        signUpMessageLabel.setTextFill(Color.RED);

        Button signUpBackButton = new Button("Back");

        VBox signupLayout = new VBox(signUpMessage, usernameSignUpLabelBox, signupUsernameField, passwordSignUpLabelBox, signupPasswordField, signUpButton, signUpMessageLabel, signUpBackButton);
        signupLayout.setSpacing(10);
        signupLayout.setPadding(new Insets(30));
        signupLayout.setAlignment(Pos.CENTER);
        Scene singupScene = new Scene(signupLayout, 700, 500);


        // Main menu scene
        BorderPane borderPane = new BorderPane();

        // Top toolbar
        Button mainMenuButton = new Button("Main Menu");
        HBox toolBar = new HBox(mainMenuButton);
        toolBar.setSpacing(10);
        toolBar.setPadding(new Insets(10));
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        toolBar.setStyle("-fx-background-color: #555555;");

        // Left main menu
        Label topScore = new Label("Top Scores");
        topScore.setStyle("-fx-font-weight: bold");
        Label blackjackScores = new Label("Blackjack:");
        Label snakeScores = new Label("Snake:");
        VBox mainMenuLeft = new VBox(topScore, blackjackScores, snakeScores);
        mainMenuLeft.setSpacing(10);
        mainMenuLeft.setPadding(new Insets(20));

        // Right main menu
        Label gameMenu = new Label("Play Games");
        gameMenu.setStyle("-fx-font-weight: bold");
        Button blackjackButton = new Button("Blackjack");
        Button snakeButton = new Button("Snake");
        Label addGameMenu = new Label("Add Games");
        addGameMenu.setStyle("-fx-font-weight: bold");
        Button addGameButton1 = new Button("Add Game");
        Button addGameButton2 = new Button("Add Game");
        VBox mainMenuRight = new VBox(gameMenu, blackjackButton, snakeButton, addGameMenu, addGameButton1, addGameButton2);
        mainMenuRight.setSpacing(10);
        mainMenuRight.setPadding(new Insets(20));

        HBox mainMenu = new HBox(mainMenuLeft, mainMenuRight);
        mainMenu.setSpacing(60);
        mainMenu.setPadding(new Insets(40));
        mainMenu.setAlignment(Pos.TOP_CENTER);

        borderPane.setTop(toolBar);
        borderPane.setCenter(mainMenu);

        Scene mainMenuScene = new Scene(borderPane, 700, 500);


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
            primaryStage.setScene(singupScene);
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
                        signUpMessageLabel.setText("Sign Up Successful! Please sign in.");
                        break;
                    case USER_TAKEN:
                        signUpMessageLabel.setText("Username taken. Please try again.");
                        break;
                    case PASSWORD_LENGTH:
                        signUpMessageLabel.setText("Password length must be longer than 8. Please try again.");
                        break;
                    case COLON_SYMBOL:
                        signUpMessageLabel.setText("Username and password cannot contain colon. Please try again.");
                        break;
                    case FILE_ERROR:
                        signUpMessageLabel.setText("File error. Please try again.");
                        break;
                }
            }
        });

        // 4 - Sign Up scene: Back button clicked
        signUpBackButton.setOnAction(e -> {
            primaryStage.setScene(loginScene);
        });

        // 5 - Tool bar "Main Menu" button
        mainMenuButton.setOnAction(e -> {
            primaryStage.setScene(mainMenuScene);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
