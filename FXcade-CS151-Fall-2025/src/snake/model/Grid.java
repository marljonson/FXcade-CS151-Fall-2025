package snake.model;

public class Grid {
    private int w;
    private int h;

    public Grid(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public boolean outOfBounds(int x, int y) {
        return (x < 0 || x >= w || y < 0 || y >= h);
    }

    public int getW() { return w; }
    public int getH() { return h; }
}
