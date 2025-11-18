import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        Label welcomeMessage = new Label("Welcome to FXcade!");

        TextField loginUsernameField = new TextField();
        loginUsernameField.setPromptText("Username");

        TextField loginPasswordField = new TextField();
        loginPasswordField.setPromptText("Password");

        Label loginMessageLabel = new Label();

        Button loginButton = new Button("Sign In");

        Label createAccountMessage = new Label("Don't have an account yet?");

        Button createAccountButton = new Button("Sign Up");

        VBox signInlayout = new VBox(welcomeMessage, loginUsernameField, loginPasswordField, loginButton, loginMessageLabel, createAccountMessage, createAccountButton);
        Scene loginScene = new Scene(signInlayout, 700, 500);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("FXcade Game Manager");
        primaryStage.show();


        // Sign up scene
        Label signUpMessage = new Label("Please fill in all the fields to sign up.");

        TextField signupUsernameField = new TextField();
        signupUsernameField.setPromptText("Username");

        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");

        Button signUpButton = new Button("Sign up");

        Label signUpMessageLabel = new Label();

        Button signUpBackButton = new Button("Back");

        VBox signupLayout = new VBox(signUpMessage, signupUsernameField, signupPasswordField, signUpButton, signUpMessageLabel,signUpBackButton);
        Scene singupScene = new Scene(signupLayout, 700, 500);


        // Main menu scene
        Label topScore = new Label("Top Scores");
        Label blackjackScores = new Label("Blackjack:");
        Label snakeScores = new Label("Snake:");
        VBox mainMenuLeft = new VBox(topScore, blackjackScores, snakeScores);

        Label gameMenu = new Label("Play Games");
        Button blackjackButton = new Button("Blackjack");
        Button snakeButton = new Button("Snake");
        VBox mainMenuRight = new VBox(gameMenu, blackjackButton, snakeButton);

        HBox mainMenu = new HBox(mainMenuLeft, mainMenuRight);

        Scene mainMenuScene = new Scene(mainMenu, 700, 500);


        // Button actions
        // 1 - Login Scene: Sign in button clicked
        loginButton.setOnAction(event -> {
            String loginUsername = loginUsernameField.getText();
            String loginPassword = loginPasswordField.getText();

            if (loginUsername.isEmpty() || loginPassword.isEmpty()) {
                loginMessageLabel.setText("Please enter both username and password.");
            }
            else {
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
            }
            else {
                AccountManager.signUpStatus signUpStatus = accountManager.createUser(signupUsername, signupPassword);
                switch (signUpStatus) {
                    case SUCCESS:
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

        // 3 - Main menu button
        // loginButton.setOnAction(e -> {
        //    primaryStage.setScene(mainMenuScene);
        // });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
