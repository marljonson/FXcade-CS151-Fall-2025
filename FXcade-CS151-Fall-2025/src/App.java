import javafx.application.Application;
import javafx.stage.Stage;
import snake.controller.SnakeController;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SnakeController controller = new SnakeController(primaryStage);

        primaryStage.setScene(controller.getView().getScene());

        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        primaryStage.getScene().getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
