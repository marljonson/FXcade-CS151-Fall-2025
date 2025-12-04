package snake;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import snake.model.Direction;
import snake.model.Point;
import snake.model.Snake;
import snake.model.SnakeSegment;

import static org.junit.jupiter.api.Assertions.*;

public class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp(){
        snake = new Snake(5, 5, Direction.UP);
    }

    @Test
    void constructorCreatesThreeSegmentsFacingUp(){
        List<SnakeSegment> segments = snake.getSegments();
        assertEquals(3, segments.size());

        assertEquals(new Point(5, 5), segments.get(0).getPosition()); // head
        assertEquals(new Point(5, 6), segments.get(1).getPosition()); 
        assertEquals(new Point(5, 7), segments.get(2).getPosition()); 

        assertEquals(Direction.UP, snake.getCurrentDirection());
        assertEquals(Direction.UP, snake.getNextDirection());
    }

    @Test
    void setNextDirectionCannotReverse(){
        snake.setNextDirection(Direction.DOWN); // this is reverse from UP and not allowed
        assertEquals(Direction.UP, snake.getNextDirection()); // should still be UP and ignore above
    }

    @Test
    void setNextDirectionCanTurnLeftOrRight(){
        snake.setNextDirection(Direction.LEFT);
        assertEquals(Direction.LEFT, snake.getNextDirection());
        snake.updateDirection();
        assertEquals(Direction.LEFT, snake.getCurrentDirection());
    }

    @Test
    void moveAddsNewHeadInFront(){
        Point newHead = new Point(5, 4); // go up

        int oldLength = snake.getLength();
        snake.move(newHead);

        assertEquals(newHead, snake.getHeadPosition());
        assertEquals(newHead, snake.getSegments().get(0).getPosition());
        assertEquals(oldLength + 1, snake.getLength());
    }


}
