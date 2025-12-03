package snake.model;

import java.util.List;
import java.util.Random;

public class Food {

    private Point pos;
    private Random rand = new Random();
    private int w;
    private int h;

    public Food(int w, int h) {
        this.w = w;
        this.h = h;
      
        pos = new Point(0, 0);
    }

    public Point getPosition() {
        return pos;
    }

    public void spawn(List<Point> taken) {
        int x, y;
        Point p;

        do {
            x = rand.nextInt(w);
            y = rand.nextInt(h);
            p = new Point(x, y);
        } while (taken.contains(p));

        pos = p;
    }

    public boolean isAt(Point p) {
        return pos.equals(p);
    }
}
