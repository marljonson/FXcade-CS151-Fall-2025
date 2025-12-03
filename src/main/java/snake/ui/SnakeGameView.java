package snake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import snake.model.*;

import java.util.List;

public class SnakeGameView {

    private static final double WINDOW_WIDTH = 700;
    private static final double WINDOW_HEIGHT = 500;

    private BorderPane root;
    private VBox headerBox;

    private Canvas canvas;
    private GraphicsContext gc;
    private Label scoreLabel;
    private Label highScoreLabel;
    private StackPane gameOverOverlay;
    private Scene gameScene;
    private Stage stage;

    public SnakeGameView(Stage stage) {
        this.stage = stage;
        createUI();
    }

    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F5DC;");

        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(12, 20, 12, 20));
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setStyle("-fx-background-color: #E8E8D8; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(new Font("System", 16));
        scoreLabel.setTextFill(Color.rgb(50, 50, 50));

        highScoreLabel = new Label("High Score: 0");
        highScoreLabel.setFont(new Font("System", 16));
        highScoreLabel.setTextFill(Color.rgb(50, 50, 50));

        topPanel.getChildren().addAll(scoreLabel, new Label("  |  "), highScoreLabel);
        root.setTop(topPanel);

        headerBox = new VBox();
        headerBox.getChildren().add(topPanel);
        root.setTop(headerBox);

        canvas = new Canvas(Grid.CANVAS_WIDTH, Grid.CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        gameOverOverlay = createGameOverOverlay();
        StackPane gameContainer = new StackPane(canvas, gameOverOverlay);
        gameContainer.setPadding(new Insets(10));
        gameContainer.setStyle("-fx-background-color: #F5F5DC;");
        root.setCenter(gameContainer);

        // gameScene = new Scene(root, Grid.CANVAS_WIDTH + 20, Grid.CANVAS_HEIGHT + 120);
        gameScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        canvas.setFocusTraversable(true);
        canvas.requestFocus();
    }

    private StackPane createGameOverOverlay() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-background-radius: 15;");
        overlay.setVisible(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(new Font("System", 36));
        gameOverLabel.setTextFill(Color.rgb(200, 50, 50));

        Label finalScoreLabel = new Label();
        finalScoreLabel.setFont(new Font("System", 24));
        finalScoreLabel.setTextFill(Color.rgb(245, 245, 220));
        finalScoreLabel.setId("finalScoreLabel");

        Label highScoreUpdateLabel = new Label();
        highScoreUpdateLabel.setFont(new Font("System", 20));
        highScoreUpdateLabel.setTextFill(Color.rgb(255, 255, 150));
        highScoreUpdateLabel.setId("highScoreUpdateLabel");

        Button restartButton = new Button("Restart");
        restartButton.setFont(new Font("System", 18));
        restartButton.setPrefWidth(150);
        restartButton.setPrefHeight(40);
        restartButton.setStyle("-fx-background-color: #90EE90; " +
                "-fx-background-radius: 10; " +
                "-fx-text-fill: #2F4F2F;");
        restartButton.setId("restartButton");

        /*
        Button menuButton = new Button("Main Menu");
        menuButton.setFont(new Font("System", 18));
        menuButton.setPrefWidth(150);
        menuButton.setPrefHeight(40);
        menuButton.setStyle("-fx-background-color: #E8E8D8; " +
                "-fx-background-radius: 10; " +
                "-fx-text-fill: #2F2F2F;");
        menuButton.setId("menuButton");
         */

        content.getChildren().addAll(gameOverLabel, finalScoreLabel, highScoreUpdateLabel, restartButton);
        overlay.getChildren().add(content);

        return overlay;
    }

    public void render(Snake snake, Food food, boolean paused, boolean gameOver) {
        // Clear canvas
        gc.setFill(Color.rgb(245, 245, 220));
        gc.fillRoundRect(0, 0, Grid.CANVAS_WIDTH, Grid.CANVAS_HEIGHT, Grid.CORNER_RADIUS, Grid.CORNER_RADIUS);

        // Draw grid lines
        gc.setStroke(Color.rgb(210, 200, 180));
        gc.setLineWidth(0.5);
        for (int i = 0; i <= Grid.GRID_WIDTH; i++) {
            double x = i * Grid.GRID_SIZE;
            gc.strokeLine(x, 0, x, Grid.CANVAS_HEIGHT);
        }
        for (int i = 0; i <= Grid.GRID_HEIGHT; i++) {
            double y = i * Grid.GRID_SIZE;
            gc.strokeLine(0, y, Grid.CANVAS_WIDTH, y);
        }

        if (paused) {
            gc.setFill(Color.rgb(100, 100, 100));
            gc.setFont(new Font("System", 36));
            gc.fillText("PAUSED", Grid.CANVAS_WIDTH / 2 - 80, Grid.CANVAS_HEIGHT / 2);
            return;
        }

        if (gameOver) {
            return;
        }

        Point foodPos = food.getPosition();
        drawApple(foodPos.x * Grid.GRID_SIZE + Grid.GRID_SIZE / 2,
                foodPos.y * Grid.GRID_SIZE + Grid.GRID_SIZE / 2,
                Grid.GRID_SIZE * 0.7);

        List<SnakeSegment> segments = snake.getSegments();
        for (int i = 0; i < segments.size(); i++) {
            SnakeSegment segment = segments.get(i);
            double x = segment.getX() * Grid.GRID_SIZE;
            double y = segment.getY() * Grid.GRID_SIZE;

            if (i == 0) {
                drawSnakeHead(x, y, snake.getCurrentDirection());
            } else {
                drawSnakeSegment(x, y);
            }
        }
    }

    private void drawSnakeSegment(double x, double y) {
        gc.setFill(Color.rgb(144, 238, 144));
        gc.fillRoundRect(x + 2, y + 2, Grid.GRID_SIZE - 4, Grid.GRID_SIZE - 4, 5, 5);

        gc.setStroke(Color.rgb(100, 180, 100));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x + 2, y + 2, Grid.GRID_SIZE - 4, Grid.GRID_SIZE - 4, 5, 5);
    }

    private void drawSnakeHead(double x, double y, Direction direction) {
        drawSnakeSegment(x, y);

        double eyeX, eyeY;
        double eyeSize = 3;

        switch (direction) {
            case UP:
                eyeX = x + Grid.GRID_SIZE * 0.3;
                eyeY = y + Grid.GRID_SIZE * 0.3;
                break;
            case DOWN:
                eyeX = x + Grid.GRID_SIZE * 0.3;
                eyeY = y + Grid.GRID_SIZE * 0.7;
                break;
            case LEFT:
                eyeX = x + Grid.GRID_SIZE * 0.3;
                eyeY = y + Grid.GRID_SIZE * 0.5;
                break;
            case RIGHT:
            default:
                eyeX = x + Grid.GRID_SIZE * 0.7;
                eyeY = y + Grid.GRID_SIZE * 0.5;
                break;
        }

        gc.setFill(Color.BLACK);
        gc.fillOval(eyeX - eyeSize / 2, eyeY - eyeSize / 2, eyeSize, eyeSize);
    }

    private void drawApple(double centerX, double centerY, double size) {
        gc.setFill(Color.rgb(220, 50, 50));
        gc.fillOval(centerX - size / 2, centerY - size / 2, size, size);

        gc.setFill(Color.rgb(255, 200, 200));
        gc.fillOval(centerX - size / 3, centerY - size / 2.5, size / 3, size / 3);

        gc.setFill(Color.rgb(139, 90, 43));
        gc.setLineWidth(2);
        gc.strokeLine(centerX, centerY - size / 2, centerX, centerY - size / 2 - 4);
        gc.fillRect(centerX - 1, centerY - size / 2 - 4, 2, 4);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateHighScore(int highScore) {
        highScoreLabel.setText("High Score: " + highScore);
    }

    public void showGameOver(int score, int highScore, boolean isNewHighScore) {
        Label finalScoreLabel = (Label) gameOverOverlay.lookup("#finalScoreLabel");
        Label highScoreUpdateLabel = (Label) gameOverOverlay.lookup("#highScoreUpdateLabel");

        if (finalScoreLabel != null) {
            finalScoreLabel.setText("Final Score: " + score);
        }

        if (highScoreUpdateLabel != null) {
            if (isNewHighScore) {
                highScoreUpdateLabel.setText("New High Score!");
            } else {
                highScoreUpdateLabel.setText("High Score: " + highScore);
            }
        }

        gameOverOverlay.setVisible(true);
    }

    public void setToolbar(HBox toolbar) {
        if (headerBox == null) return;

        headerBox.getChildren().add(0, toolbar);
    }

    public void hideGameOver() {
        gameOverOverlay.setVisible(false);
    }

    public Button getRestartButton() {
        return (Button) gameOverOverlay.lookup("#restartButton");
    }

    /* public Button getMenuButton() {
        return (Button) gameOverOverlay.lookup("#menuButton");
    }
     */

    public Canvas getCanvas() {
        return canvas;
    }

    public Scene getScene() {
        return gameScene;
    }
}

