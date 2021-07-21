package org.example;

import edu.princeton.cs.algs4.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class PointSET {
    SET<Node> treeSet;
    private Stack<Point2D> interaPoints = new Stack<>();
    /* lets start with a grid size of 10 */
    int gridLength = 10;
    private Point2D[][] grid = new Point2D[gridLength][gridLength];


    private class Cell {
        private List<Point2D> content = new ArrayList<Point2D>();

        private void add(Point2D p) {
            content.add(p);
        }
    }

    int matrixSize = (int) Math.sqrt((treeSet.size()));
    // int matrixSize = 20;
    Cell[][] matrix = new Cell[matrixSize][matrixSize];

    public PointSET() {
        treeSet = new SET<Node>();


    }

    private static class Node implements Comparable<Node> {
        private Point2D p;
        private double gridX;
        private double gridY;
        private Node lb;
        private Node rt;

        Node(Point2D point, int gridX, int gridY, Node leftBranch, Node rightBranch) {
            p = point;
            this.gridX = gridX;
            this.gridY = gridY;
            lb = leftBranch;
            rt = rightBranch;
        }

        @Override
        public int compareTo(Node obj) {
            if (this.p.compareTo(obj.p) < 0) return -1;
            else if (this.p.compareTo(obj.p) > 0) return 1;
            return 0;
        }
    }

    public boolean isEmpty() {
        return treeSet.isEmpty();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "insert() ");
        Node n = new Node(p, (int) p.x(), (int) p.y(), null, null);
        if (!treeSet.contains(n)) treeSet.add(n);
        Cell currentCell = new Cell();
        int multFactor = matrixSize / 10;

            currentCell.add(p);
            /* Here is another way of converting double coordinates to int matrix address
             * String numberStr = Double.toString(number);
             * String fractionalStr = numberStr.substring(numberStr.indexOf('.')+1);
             * int fractional = Integer.valueOf(fractionalStr);
             * from: https://stackoverflow.com/questions/11495565/how-to-extract-fractional-digits-of-double-bigdecimal#11495691
             * Note you should increase the decimals as the number of points and Grid cells increase */
            int decimals = 1;
            BigDecimal xvalue = new BigDecimal(p.x()).setScale(decimals, RoundingMode.DOWN);
            BigInteger XINTEGER = xvalue.abs().toBigInteger();
            BigInteger XDECIMAL = (xvalue.subtract(new BigDecimal(XINTEGER))).multiply(new BigDecimal(10).pow(decimals)).toBigInteger();
            BigDecimal yvalue = new BigDecimal(p.y()).setScale(decimals, RoundingMode.DOWN);
            BigInteger YINTEGER = yvalue.abs().toBigInteger();
            BigInteger YDECIMAL = (yvalue.subtract(new BigDecimal(YINTEGER))).multiply(new BigDecimal(10).pow(decimals)).toBigInteger();
            if (matrix[multFactor * XDECIMAL.intValueExact()][multFactor * YDECIMAL.intValueExact()] != null) {
                currentCell = matrix[multFactor * XDECIMAL.intValueExact()][multFactor * YDECIMAL.intValueExact()];
                currentCell.add(p);
                matrix[multFactor * XDECIMAL.intValueExact()][multFactor * YDECIMAL.intValueExact()] = currentCell;
            } else {
                matrix[multFactor * XDECIMAL.intValueExact()][multFactor * YDECIMAL.intValueExact()] = currentCell;
            }
            currentCell = new Cell();

    }

    private RectHV buildRect(Point2D p) {
        RectHV r;
        return r = new RectHV(p.x() - 0.01, p.y() - 0.01, p.x() + 0.01, p.y() + 0.01);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "contains() ");
        Node n = new Node(p, (int) p.x(), (int) p.y(), null, null);
        return treeSet.contains(n);

    }

    public int size() {
        if (treeSet.isEmpty()) return 0;
        return treeSet.size();
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "nearest() ");
        else if (treeSet.isEmpty()) return null;
        Point2D nearestP = null;
        Point2D point;
        Node n = new Node(p, (int)p.x(),(int)p.y(), null, null);
        if (treeSet.contains(n)) nearestP = p;
        for (Node node : treeSet) {
            if (nearestP != null && node.rect.distanceTo(p) > nearestP.distanceTo(p)) continue;
            else if ((nearestP == null) || node.p.distanceSquaredTo(p) < nearestP.distanceSquaredTo(p))
                nearestP = node.p;
        }
        return nearestP;
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.001);
        double counter;
        for (int i = 0; i < grid.length; i++) {
            counter = i;
            StdDraw.line(0.0, (1.0 - (counter / gridLength)), 1.0, (1.0 - (counter / gridLength)));
            // StdDraw.line(0.0, 0.9, 1.0, 0.9);
            for (int j = 0; j < gridLength; j++) {
                StdDraw.line((1.0 - (counter / gridLength)), 0.0, (1.0 - (counter / gridLength)), 1.0);
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Can not pass " +
                "null to range().");
        else if (isEmpty()) return null;
        interaPoints = new Stack<>();
        /* Use the tree and eliminate as much of it as you avoid searching. */
        Point2D point;
        for (Object obj : treeSet) {
            point = (Point2D) obj;
            if (rect.contains(point)) {
                interaPoints.push(point);
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
        // double timePerInsert = (time / n);
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
        double logOfPoints = Math.log(n);
        StdOut.printf("Log of %d nodes is: %4f, and contains() takes %4f to push 100000 nodes into %d " +
                "nodes.", n, logOfPoints, time2, n);
        return time2;
    }

    private static double rangeTimes(int n) {
        PointSET pSet = new PointSET();
        // Stack<Point2D> s = new Stack<>();
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
        // double timePerSetSize = time3 / n;
        double logOfPoints = Math.log(n);
        StdOut.printf("Log of %d nodes is: %4f, and range() takes %4f for set size: %n",
                n, logOfPoints, time3);
        return time3;
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointSET pSet = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            pSet.insert(p);
        }
        pSet.draw();
        /* Point2D inquiryPoint = new Point2D(0.500000, 1.000000);
        StdOut.println(" Here is the nearest point to 0.5,1.0 :" + pSet.nearest(inquiryPoint));
         Measure how long it takes to insert() and contains() - should be logarithm. nearest() and
         * range should be linear(N).
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.text(0.15, 0.98, "Insertion Times");
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
            interval += 0.015;
        }
*/
//            StdDraw.rectangle(.10, .4, .02, .1);
//
//            for (Point2D p : s) {
//                StdDraw.point(p.x(), p.y());
//            }
        // pSet.insert(p1);
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
