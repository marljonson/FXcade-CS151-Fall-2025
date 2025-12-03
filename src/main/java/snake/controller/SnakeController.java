package snake.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import snake.model.*;
import snake.ui.SnakeGameView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SnakeController {

    private final int START_SPEED = 150;
    private final int SPEED_UP = 5;

    private Snake snake;
    private Food food;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private int score = 0;
    private int highScore = 0;
    private int speed;
    private Random rand = new Random();
    private Timeline loop;

    private SnakeGameView view;
    private Stage stage;
    private Runnable onMainMenu;

    public SnakeController(Stage stage) {
        this.stage = stage;
        view = new SnakeGameView(stage);

        loadHighScore();
        resetGame();
        setupControls();
        startLoop();

        view.render(snake, food, isPaused, isGameOver);
    }


    public SnakeController(Stage stage, Runnable onMainMenu) {
        this.stage = stage;
        this.onMainMenu = onMainMenu;
        view = new SnakeGameView(stage);

        loadHighScore();
        resetGame();
        setupControls();
        startLoop();

        view.render(snake, food, isPaused, isGameOver);
    }

    private void resetGame() {
        Point mid = Grid.getCenter();
        Direction[] dirs = Direction.values();

        Direction startDir = dirs[rand.nextInt(dirs.length)];

        snake = new Snake(mid.x, mid.y, startDir);
        food = new Food(Grid.GRID_WIDTH, Grid.GRID_HEIGHT);

        ArrayList<Point> taken = new ArrayList<>();
        for (var s : snake.getSegments()) {
            taken.add(s.getPosition());
        }

        food.spawn(taken);

        isGameOver = false;
        isPaused = false;
        score = 0;
        speed = START_SPEED;

        view.updateScore(score);
        view.updateHighScore(highScore);
    }

    private void setupControls() {
        // view.getScene().setOnKeyPressed(this::handleKeys);
        view.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeys);

        view.getRestartButton().setOnAction(e -> restart());
        /*view.getMenuButton().setOnAction(e -> {
            if (loop != null) {
                loop.stop();
            }
            if (onMainMenu != null) {
                onMainMenu.run();
            }
        });
         */
    }

    private void handleKeys(KeyEvent e) {

        if (isGameOver) return;

        KeyCode key = e.getCode();

        if (key == KeyCode.ESCAPE) {
            isPaused = !isPaused;

            if (isPaused) loop.pause();
            else loop.play();

            view.render(snake, food, isPaused, isGameOver);
            return;
        }

        if (isPaused) return;

        switch (key) {
            case UP:
                snake.setNextDirection(Direction.UP);
                break;
            case DOWN:
                snake.setNextDirection(Direction.DOWN);
                break;
            case LEFT:
                snake.setNextDirection(Direction.LEFT);
                break;
            case RIGHT:
                snake.setNextDirection(Direction.RIGHT);
                break;
        }
    }

    private void startLoop() {
        loop = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            if (!isPaused && !isGameOver) {
                updateGame();
                view.render(snake, food, isPaused, isGameOver);
            }
        }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();
    }

    private void updateGame() {
        snake.updateDirection();

        Point curr = snake.getHeadPosition();
        Point next = getNextPos(curr, snake.getCurrentDirection());

        if (!Grid.isWithinBounds(next)) {
            gameOver();
            return;
        }

        if (snake.collidesWithSelf(next)) {
            gameOver();
            return;
        }

        snake.move(next);

        if (food.isAt(next)) {
            score += 10;
            view.updateScore(score);

            speed = Math.max(START_SPEED - (score / 10) * SPEED_UP, 50);

            loop.stop();
            startLoop();

            ArrayList<Point> used = new ArrayList<>();
            for (var s : snake.getSegments()) used.add(s.getPosition());

            food.spawn(used);

        } else {
            snake.removeTail();
        }
    }

    private Point getNextPos(Point p, Direction d) {
        int x = p.x, y = p.y;

        switch (d) {
            case UP:
                y--;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        return new Point(x, y);
    }

    private void gameOver() {
        isGameOver = true;
        loop.stop();

        boolean newHS = score > highScore;

        if (newHS) {
            highScore = score;
            saveHighScore();
            view.updateHighScore(highScore);
        }

        view.showGameOver(score, highScore, newHS);
        view.render(snake, food, isPaused, isGameOver);
    }

    private void restart() {
        view.hideGameOver();
        if (loop != null) loop.stop();
        resetGame();
        startLoop();
        view.render(snake, food, isPaused, isGameOver);

        view.getCanvas().requestFocus();
    }

    private void loadHighScore() {
        try {
            Path p = Paths.get("data/high_scores.txt");
            if (!Files.exists(p)) {
                highScore = 0;
                return;
            }

            List<String> lines = Files.readAllLines(p);
            for (String line : lines) {
                if (line.startsWith("Snake:")) {
                    try {
                        highScore = Integer.parseInt(line.substring(6).trim());
                    } catch (Exception ex) {
                        highScore = 0;
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Couldn't read high score file.");
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try {
            Path p = Paths.get("data/high_scores.txt");
            List<String> list = new ArrayList<>();

            if (Files.exists(p)) {
                list = Files.readAllLines(p);
            }

            boolean found = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).startsWith("Snake:")) {
                    list.set(i, "Snake: " + highScore);
                    found = true;
                    break;
                }
            }

            if (!found) list.add("Snake: " + highScore);

            Files.write(p, list);

        } catch (IOException e) {
            System.out.println("Error writing high score.");
        }
    }

    public SnakeGameView getView() {
        return view;
    }
}
