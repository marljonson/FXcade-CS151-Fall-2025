import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        // Login scene
        Label welcomeMessage = new Label("Welcome to FxCade!");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Sign In");

        Label createAccountMessage = new Label("Don't have an account yet?");

        Button createAccountButton = new Button("Create Account");

        VBox layout = new VBox(welcomeMessage, usernameField, passwordField, loginButton, createAccountMessage, createAccountButton);
        Scene LoginScene = new Scene(layout, 300, 200);

        primaryStage.setScene(LoginScene);
        primaryStage.setTitle("FXCade Game Manager");
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
