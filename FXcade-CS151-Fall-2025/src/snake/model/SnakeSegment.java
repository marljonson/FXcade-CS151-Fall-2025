package snake.model;

public class SnakeSegment {
    private Point position;
    
    public SnakeSegment(Point position) {
        this.position = position;
    }
    
    public SnakeSegment(int x, int y) {
        this.position = new Point(x, y);
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point position) {
        this.position = position;
    }
    
    public int getX() {
        return position.x;
    }
    
    public int getY() {
        return position.y;
    }
}

