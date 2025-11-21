package snake.controller;

import javafx.animation.AnimationTimer;
import snake.model.*;
import snake.ui.SnakeGameView;

import java.util.Random;

public class SnakeController {

    private Snake snake;
    private Grid grid;
    private Food food;

    private boolean gameOver = false;

    private SnakeGameView view;

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

        food = new Food(x, y);
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
            speed++; 
        }

        snake.move(grow);
        SnakeSegment newHead = snake.getHead();

        if (grid.outOfBounds(newHead.getX(), newHead.getY())) {
            gameOver = true;
            view.showGameOver();
            return;
        }

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
