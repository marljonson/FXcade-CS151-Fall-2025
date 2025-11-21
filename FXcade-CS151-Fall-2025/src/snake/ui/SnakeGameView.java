package snake.ui;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import snake.controller.SnakeController;
import snake.model.*;

public class SnakeGameView {

    private Canvas canvas;
    private GraphicsContext gc;

    private int cellSize = 25;
    private int w = 20;
    private int h = 20;

    public void start(Stage stage) {
        canvas = new Canvas(w * cellSize, h * cellSize);
        gc = canvas.getGraphicsContext2D();

        VBox root = new VBox(canvas);
        Scene scene = new Scene(root);

        SnakeController controller = new SnakeController(this, w, h);
        controller.start();

        // WASD like your original
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> controller.setDir(Direction.UP);
                case S -> controller.setDir(Direction.DOWN);
                case A -> controller.setDir(Direction.LEFT);
                case D -> controller.setDir(Direction.RIGHT);
            }
        });

        stage.setScene(scene);
        stage.setTitle("Snake Game");
        stage.show();
    }

    public void draw(Snake snake, Food food) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, w * cellSize, h * cellSize);

        gc.setFill(Color.RED);  
        gc.fillOval(food.getX() * cellSize, food.getY() * cellSize, cellSize, cellSize);


        for (SnakeSegment s : snake.getBody()) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(s.getX() * cellSize, s.getY() * cellSize, cellSize - 1, cellSize - 1);

            gc.setFill(Color.GREEN);
            gc.fillRect(s.getX() * cellSize, s.getY() * cellSize, cellSize - 2, cellSize - 2);
        }
    }

    public void showGameOver() {
        gc.setFill(Color.RED);
        gc.setFont(new Font("", 50));
        gc.fillText("GAME OVER", 100, 250);
    }
}
