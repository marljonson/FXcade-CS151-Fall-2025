package snake.model;

public class Grid {
    public static final int GRID_SIZE = 30; 
    public static final int GRID_WIDTH = 15; 
    public static final int GRID_HEIGHT = 15; 
    public static final int CANVAS_WIDTH = GRID_WIDTH * GRID_SIZE;
    public static final int CANVAS_HEIGHT = GRID_HEIGHT * GRID_SIZE;
    public static final int CORNER_RADIUS = 15; 
    
    public static boolean isWithinBounds(Point point) {
        return point.x >= 0 && point.x < GRID_WIDTH &&
               point.y >= 0 && point.y < GRID_HEIGHT;
    }
    
    public static Point getCenter() {
        return new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2);
    }
}

