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
        Label welcomeMessage = new Label("Welcome to FxCade!");

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
        primaryStage.setTitle("FXCade Game Manager");
        primaryStage.show();


        // Sign up scene
        Label signUpMessage = new Label("Please fill in all the fields to sign up.");

        TextField signupUsernameField = new TextField();
        signupUsernameField.setPromptText("Username");

        TextField signupPasswordField = new TextField();
        signupPasswordField.setPromptText("Password");

        Button signUpButton = new Button("Sign up");

        VBox signupLayout = new VBox(signUpMessage, signupUsernameField, signupPasswordField, signUpButton);
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
        // 1 - Sign in
        loginButton.setOnAction(event -> {
            String username = loginUsernameField.getText();
            String password = loginPasswordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                loginMessageLabel.setText("Please enter both username and password.");
            }
            else {
                AccountManager.loginStatus signInStatus = accountManager.signIn(username, password);
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

        // 2 - Sign Up
        createAccountButton.setOnAction(e -> {
            primaryStage.setScene(singupScene);
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
