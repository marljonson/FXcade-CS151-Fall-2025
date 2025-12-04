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
    private final String username;
    private static final int MAX_TOP_SCORES = 5;

    private Snake snake;
    private Food food;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private int score = 0;
    private int highScore = 0;
    private List<Integer> topScores = new ArrayList<>();
    private int speed;
    private Random rand = new Random();
    private Timeline loop;

    private SnakeGameView view;
    private Stage stage;
    private Runnable onMainMenu;

    public SnakeController(Stage stage, String username) {
        this.stage = stage;
        this.username = username;
        view = new SnakeGameView(stage);

        loadHighScores();
        resetGame();
        setupControls();
        startLoop();

        view.render(snake, food, isPaused, isGameOver);
    }


    public SnakeController(Stage stage, String username, Runnable onMainMenu) {
        this.stage = stage;
        this.username = username;
        this.onMainMenu = onMainMenu;
        view = new SnakeGameView(stage);

        loadHighScores();
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
        view.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeys);

        view.getRestartButton().setOnAction(e -> restart());

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

        boolean newHS = updateTopScores(score);

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

    private void loadHighScores() {
        topScores.clear();
        highScore = 0;

        try {
            Path p = Paths.get("data/high_scores.txt");

            List<String> lines = Files.readAllLines(p);
            String prefix = username + ":snake:";

            for (String line : lines) {
                if (!line.startsWith(prefix)) continue;

                // Format: username:snake:top1:top2:top3:top4:top5
                String[] parts = line.split(":");
                // parts[0] = username
                // parts[1] = "snake"
                for (int i = 2; i < parts.length; i++) {
                    if (parts[i].isBlank()) continue;
                    try {
                        int s = Integer.parseInt(parts[i]);
                        topScores.add(s);
                    } catch (NumberFormatException ignored) {
                    }
                }
                // user's snake scores found
                break;
            }

            topScores.sort(Comparator.reverseOrder());
            while (topScores.size() > MAX_TOP_SCORES) {
                topScores.remove(topScores.size() - 1);
            }

            if (!topScores.isEmpty()) {
                highScore = topScores.get(0);
            }

        } catch (Exception e) {
            System.out.println("Couldn't read high scores file.");
            topScores.clear();
            highScore = 0;
        }
    }

    private boolean updateTopScores(int newScore) {
        int lastBest = highScore;

        topScores.add(newScore);
        topScores.sort(Comparator.reverseOrder());

        while (topScores.size() > MAX_TOP_SCORES) {
            topScores.remove(topScores.size() - 1);
        }

        highScore = topScores.isEmpty() ? 0 : topScores.get(0);

        saveHighScores();
        view.updateHighScore(highScore);

        return highScore > lastBest;
    }


    private void saveHighScores() {
        try {
            Path p = Paths.get("data/high_scores.txt");
            List<String> lines;

            if (Files.exists(p)) {
                lines = new ArrayList<>(Files.readAllLines(p));
            } else {
                lines = new ArrayList<>();
            }

            String prefix = username + ":snake:";

            // Build updated line: username:snake:top1:top2:top3:top4:top5
            StringBuilder sb = new StringBuilder();
            sb.append(username).append(":snake");
            for (int i = 0; i < MAX_TOP_SCORES; i++) {
                sb.append(":");
                if (i < topScores.size()) {
                    sb.append(topScores.get(i));
                } else {
                    sb.append("0");
                }
            }
            String newLine = sb.toString();

            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(prefix)) {
                    lines.set(i, newLine);
                    found = true;
                    break;
                }
            }

            if (!found) {
                lines.add(newLine);
            }

            Files.write(p, lines);

        } catch (IOException e) {
            System.out.println("Error writing high scores for user: " + username);
        }
    }

    public SnakeGameView getView() {
        return view;
    }
}
