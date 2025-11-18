package snake.model;

import java.util.ArrayList;
import java.util.List;

public class Snake {

    private List<SnakeSegment> body = new ArrayList<>();
    private Direction dir;

    public Snake(int startX, int startY, Direction d) {
        this.dir = d;

        body.add(new SnakeSegment(startX, startY));
        body.add(new SnakeSegment(startX, startY));
        body.add(new SnakeSegment(startX, startY));
    }

    public List<SnakeSegment> getBody() {
        return body;
    }

    public Direction getDirection() {
        return dir;
    }

    public void setDirection(Direction d) {
        this.dir = d;
    }

    public SnakeSegment getHead() {
        return body.get(0);
    }

    public void move(boolean grow) {
        SnakeSegment head = getHead();
        int newX = head.getX();
        int newY = head.getY();

        switch (dir) {
            case UP -> newY--;
            case DOWN -> newY++;
            case LEFT -> newX--;
            case RIGHT -> newX++;
        }

        body.add(0, new SnakeSegment(newX, newY));  

        if (!grow) {
            body.remove(body.size() - 1);         
        }
    }
}
