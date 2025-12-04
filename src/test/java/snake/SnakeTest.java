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


}
