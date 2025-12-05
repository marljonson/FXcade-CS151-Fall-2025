package snake.model;

import java.util.ArrayList;
import java.util.List;

public class Snake {

    private List<SnakeSegment> body;
    private Direction dir;
    private Direction nextDir;

    public Snake(int cx, int cy, Direction startDir) {
        body = new ArrayList<>();
        dir = startDir;
        nextDir = startDir;

        body.add(new SnakeSegment(cx, cy));

        if (startDir == Direction.UP) {
            body.add(new SnakeSegment(cx, cy + 1));
            body.add(new SnakeSegment(cx, cy + 2));
        } else if (startDir == Direction.DOWN) {
            body.add(new SnakeSegment(cx, cy - 1));
            body.add(new SnakeSegment(cx, cy - 2));
        } else if (startDir == Direction.LEFT) {
            body.add(new SnakeSegment(cx + 1, cy));
            body.add(new SnakeSegment(cx + 2, cy));
        } else if (startDir == Direction.RIGHT) {
            body.add(new SnakeSegment(cx - 1, cy));
            body.add(new SnakeSegment(cx - 2, cy));
        }
    }

    public List<SnakeSegment> getSegments() {
        return body;
    }

    public SnakeSegment getHead() {
        return body.get(0);
    }

    public Point getHeadPosition() {
        return getHead().getPosition();
    }

    public Direction getCurrentDirection() {
        return dir;
    }

    public Direction getNextDirection() {
        return nextDir;
    }

    public void setNextDirection(Direction d) {
        // can't reverse or snake dies
        if (dir == Direction.UP && d == Direction.DOWN) return;
        if (dir == Direction.DOWN && d == Direction.UP) return;
        if (dir == Direction.LEFT && d == Direction.RIGHT) return;
        if (dir == Direction.RIGHT && d == Direction.LEFT) return;

        nextDir = d;
    }

    public void updateDirection() {
        dir = nextDir;
    }

    public void move(Point newHead) {
        body.add(0, new SnakeSegment(newHead));
    }

    public void grow() {

    }

    public void removeTail() {
        if (body.size() > 1) {
            body.remove(body.size() - 1);
        }
    }

    public boolean collidesWithSelf(Point newPos) {
        for (SnakeSegment s : body) {
            if (s.getPosition().equals(newPos)) {
                return true;
            }
        }
        return false;
    }

    public int getLength() {
        return body.size();
    }
}