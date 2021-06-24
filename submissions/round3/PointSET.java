
import edu.princeton.cs.algs4.*;

import java.awt.*;
import java.util.TreeSet;

public class PointSET {
    private SET<Node> treeSet;
    private Stack<Point2D> interaPoints = new Stack<>();

    private static class Node implements Comparable<Node> {
        private final Point2D p;
        private final RectHV rect;
        private Node lb;
        private Node rt;

        public Node(Point2D p, RectHV rect, Node lb, Node rt) {
            this.p = p;
            this.rect = rect;
            this.lb = lb;
            this.rt = rt;
        }

        /* Points are alreday compared; maybe compare the rectangles here? */
        @Override
        public int compareTo(Node o) {
            if (this.rect.xmin() < o.rect.xmin() && this.rect.xmax() < o.rect.xmax()) return -1;
            if (this.rect.ymin() < o.rect.ymin() && this.rect.ymax() < o.rect.ymax()) return -1;
            if (this.rect.xmin() > o.rect.xmin() && this.rect.xmax() > o.rect.xmax()) return 1;
            if (this.rect.ymin() > o.rect.ymin() && this.rect.ymax() > o.rect.ymax()) return 1;
            if (this.p.x() > o.p.x() && this.p.y() > o.p.y()) return 1;
            if (this.p.x() < o.p.x() && this.p.x() < o.p.x()) return -1;
            else {
//                StdOut.println("Have another look at breaking a tie. Put a break point or print the" +
//                        "objects and see if they are equal, and if not, why they get here. ");
                return 0;
            }

        }
    }

    public PointSET() {
        treeSet = new SET<Node>();
    }

    public boolean isEmpty() {
        return treeSet.size() == 0;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to insert() ");
        RectHV newRecHV = new RectHV(p.x() - 0.02, p.y() - 0.02, p.x() +
                0.02, p.y() + 0.02);
        Node n = new Node(p, newRecHV, null, null);
        treeSet.add(n);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to contains() ");
        for (Node n : treeSet) {
            if (n.rect.contains(p)) {
                if (n.p.equals(p)) return true;
            }
        }
        return false;
    }

    public int size() {
        return treeSet.size();
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to nearest() ");
        Point2D nearestP = new Point2D(0, 0);
        nearestP = treeSet.min().p;
        for (Node n : treeSet) {
            if (n.p.distanceTo(p) < nearestP.distanceTo(p)) nearestP = n.p;
        }
        return p;
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (Node n : treeSet) {
            StdDraw.point(n.p.x(), n.p.y());
            StdDraw.setPenColor(StdDraw.RED);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect.equals(null)) throw new IllegalArgumentException("Can not pass null to range().");
        /* Use the tree and eliminate as much of it as you avoid searching. */
        for (Node n : treeSet) {
            if (n.rect.intersects(rect)) {
                if (rect.contains(n.p)) {
                    interaPoints.push(n.p);
                }
            }
        }
        return interaPoints;
    }

    private static double insertTime(int n) {
        /* See how long it takes to push 100000 points */
        PointSET pSet = new PointSET();
        Stack<Point2D> s = new Stack<>();
        for (int i = 0; i < 100000; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            s.push(p);
        }
        Stopwatch timer1 = new Stopwatch();
        for (Point2D p : s) {
            pSet.insert(p);
        }
        double time = timer1.elapsedTime();
        //double timePerInsert = (time / n);
        double logOfPoints = Math.log(n);
        StdOut.printf("Log of %d nodes is: %4f, and insert() takes %12f to push 10000 points into the " +
                "tree.%n", n, logOfPoints, time);
        return time;
    }

    private static double containsTime(int n) {
        /* See how long it takes to push 100000 points */
        PointSET pSet = new PointSET();
        Stack<Point2D> s = new Stack<>();
        for (int i = 0; i < 1000000; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            s.push(p);
        }
        // RectHV r = new RectHV(0.08, 0.3, 0.12, 0.5);
        Stopwatch timer2 = new Stopwatch();
        for (Point2D p : s) {
            pSet.contains(p);
        }
        double time2 = timer2.elapsedTime();
        double timerPerContains = (time2 / (n));
        double logOfPoints = Math.log(n);
        StdOut.printf("Log of %d nodes is: %4f, and contains() takes %4f to push 100000 nodes into %d " +
                "nodes.", n, logOfPoints, time2, n);
        return time2;
    }

    private static double rangeTimes(int n) {
        PointSET pSet = new PointSET();
        Stack<Point2D> s = new Stack<>();
        for (int i = 1; i < n; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0),
                    StdRandom.uniform(0.0, 1.0));
            pSet.insert(p);
        }
        /* Create a random size rectangle, and see how long range() takes */
        double xmin = 0;
        double xmax = 0;
        double ymin = 0;
        double ymax = 0;
        double temp = 0;

        xmin = StdRandom.uniform(0.0, 1.0);
        xmax = StdRandom.uniform(0.0, 1.0);
        if (xmax < xmin) {
            temp = xmax;
            xmax = xmin;
            xmin = temp;
        }
        ymin = StdRandom.uniform(0.0, 1.0);
        ymax = StdRandom.uniform(0.0, 1.0);
        if (ymax < ymin) {
            temp = ymax;
            ymax = ymin;
            ymin = temp;
        }
        RectHV r = new RectHV(xmin, ymin, xmax, ymax);
        Stopwatch timer3 = new Stopwatch();
        pSet.range(r);
        double time3 = timer3.elapsedTime();
        //double timePerSetSize = time3 / n;
        double logOfPoints = Math.log(n);
        StdOut.printf("Log of %d nodes is: %4f, and range() takes %4f for set size: %n",
                n, logOfPoints, time3);
        return time3;
    }

    public static void main(String[] args) {
        /* Measure how long it takes to insert() and contains() - should be logarithm. nearest() and
         * range should be linear(N). */
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.text(0.15, .98, "Insertion Times");
        double interval = 0.001;
        for (int i = 100; i < 10000001; i += 1000) {
            double logOfNodes = Math.log(i);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0.15, 0.95, "Log of N multiplied");
            StdDraw.point(interval, (logOfNodes / 100));
            StdDraw.setPenColor(StdDraw.RED);
            double iTime = insertTime(i);
            StdDraw.point(interval, iTime);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.text(0.15, 0.91, "Contains Times");
            double cTime = containsTime(i);
            StdDraw.point(interval, cTime);
            double rTime = rangeTimes(i);
            StdDraw.setPenColor(StdDraw.CYAN);
            StdDraw.text(0.15, 0.87, "Range Times");
            StdDraw.point(interval, rTime);
            interval += .015;
        }

//            StdDraw.rectangle(.10, .4, .02, .1);
//
//            for (Point2D p : s) {
//                StdDraw.point(p.x(), p.y());
//            }
        //pSet.insert(p1);
//        pSet.insert(new Point2D(0.0, 0.0));
//        pSet.insert(new Point2D(0.6, 0.5));
//
//
//        StdDraw.setPenColor(StdDraw.RED);
//        StdDraw.rectangle(.10, .4, .02, .1);
//
//        pSet.draw();
    }
}
