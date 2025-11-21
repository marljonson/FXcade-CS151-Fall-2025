import javafx.application.Application;
import javafx.stage.Stage;
import snake.ui.SnakeGameView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SnakeGameView view = new SnakeGameView();
        view.start(stage);  
    }

    public static void main(String[] args) {
        launch(args);
    }
}
