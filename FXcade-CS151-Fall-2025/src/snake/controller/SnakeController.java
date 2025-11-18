package snake.controller;

import javafx.animation.AnimationTimer;
import snake.model.*;
import snake.ui.SnakeGameView;

import java.util.Random;

// ===== COMMIT 7: SnakeController =====
// commit: "Added SnakeController with main game loop + collision logic"

public class SnakeController {

    private Snake snake;
    private Grid grid;
    private Food food;

    private boolean gameOver = false;

    private SnakeGameView view;

    // kinda arbitrary but same idea as your original game
    private int speed = 5;
    private int cellSize = 25;

    private Random rand = new Random();

    public SnakeController(SnakeGameView view, int w, int h) {
        this.view = view;
        this.grid = new Grid(w, h);

        int startX = w / 2;
        int startY = h / 2;

        this.snake = new Snake(startX, startY, Direction.LEFT);

        makeFood();
    }

    public void setDir(Direction d) {
        snake.setDirection(d);
    }

    private void makeFood() {
        int x = rand.nextInt(grid.getW());
        int y = rand.nextInt(grid.getH());
        int type = rand.nextInt(5);

        food = new Food(x, y, type);
    }

    public void start() {
        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    update();
                    return;
                }

                if (now - last > 1_000_000_000 / speed) {
                    last = now;
                    update();
                }
            }
        }.start();
    }

    private void update() {
        if (gameOver) return;

        SnakeSegment head = snake.getHead();

        boolean grow = (head.getX() == food.getX() && head.getY() == food.getY());
        if (grow) {
            makeFood();
            speed++; // like your old code
        }

        snake.move(grow);
        SnakeSegment newHead = snake.getHead();

        // hit wall
        if (grid.outOfBounds(newHead.getX(), newHead.getY())) {
            gameOver = true;
            view.showGameOver();
            return;
        }

        // hit itself
        for (int i = 1; i < snake.getBody().size(); i++) {
            SnakeSegment seg = snake.getBody().get(i);
            if (seg.getX() == newHead.getX() && seg.getY() == newHead.getY()) {
                gameOver = true;
                view.showGameOver();
                return;
            }
        }

        view.draw(snake, food);
    }
}
