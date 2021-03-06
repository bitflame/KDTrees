package org.example;

import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

import java.util.Iterator;


public class PointSET {
    private SET<Point2D> treeSet;
    // private Stack<Point2D> interaPoints = new Stack<>();
    private Point2D point = null;
    /* lets start with a grid size of 10. Not allowed to use bigDecimal, BigInteger, and rounding mode. So I have to
       try to use the other method if I need a grid */
    // private int gridLength = 10;
    // private Point2D[][] grid = new Point2D[gridLength][gridLength];


    /*private class Cell {
        private List<Point2D> content = new ArrayList<Point2D>();

        private void add(Point2D p) {
            content.add(p);
        }
    }

    int matrixSize;*/
    // int matrixSize = 20;
    // Cell[][] matrix = new Cell[matrixSize][matrixSize];

    public PointSET() {
        treeSet = new SET<Point2D>();
    }

    /*private static class Node implements Comparable<Node> {
        private Point2D p;
        private RectHV rect;


        // Node(Point2D point, RectHV rect) {
        Node(Point2D point) {
            p = point;
            //this.rect = rect;
        }

        @Override
        public int compareTo(Node obj) {
            if (this.p.compareTo(obj.p) < 0) return -1;
            else if (this.p.compareTo(obj.p) > 0) return 1;
            return 0;
        }
    }*/

    public boolean isEmpty() {
        return treeSet.isEmpty();
    }


    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "insert() ");
        // RectHV rect = buildRect(p);
        // Node n = new Node(p, rect);
        // Node n = new Node(p);
        treeSet.add(p);

    }

    /*private RectHV buildRect(Point2D p) {
        RectHV r;
        return r = new RectHV(p.x() - 0.01, p.y() - 0.01, p.x() + 0.01, p.y() + 0.01);
    }*/

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "contains() ");
        // RectHV rect = buildRect(p);
        // Node n = new Node(p, rect);
        // Node n = new Node(p);
        return treeSet.contains(p);

    }

    public int size() {
        if (treeSet.isEmpty()) return 0;
        return treeSet.size();
    }

    public Point2D nearest(Point2D p) {
        /* Why not build the rectangles here? after I know how many points I am dealing with? */

        if (p == null) throw new IllegalArgumentException("Can not send a null to " +
                "nearest() ");
        else if (treeSet.isEmpty()) return null;
        Point2D nearestP = null;
        // RectHV rect = buildRect(p);
        // Node n = new Node(p, rect);
        // Node n = new Node(p);
        if (treeSet.contains(p)) nearestP = p;
        /*for (Node node : treeSet) {
            if (nearestP != null && node.rect.distanceTo(p) > nearestP.distanceTo(p)) continue;
            else if ((nearestP == null) || node.p.distanceSquaredTo(p) < nearestP.distanceSquaredTo(p))
                nearestP = node.p;
        } */

        for (Iterator<Point2D> it = treeSet.iterator(); it.hasNext(); ) {
            // Node node = it.next();
            // point = node.p;
            // node.rect=buildRect(point);
            /*todo -- I do not choose sides. I think I can move building node rectangles here, and save a bunch of space.
               Plus Ido not think I need a node for this class. I can get by with the Point2D */
            /*if (nearestP != null && node.rect.distanceTo(p) > nearestP.distanceTo(p) && it.hasNext()) {
                node = it.next();
                point = node.p;
            }*/
            point = it.next();
            if ((nearestP == null) || point.distanceSquaredTo(p) < nearestP.distanceSquaredTo(p))
                nearestP = point;
        }
        return nearestP;
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.001);
       /* double counter;
        for (int i = 0; i < grid.length; i++) {
            counter = i;
            StdDraw.line(0.0, (1.0 - (counter / gridLength)), 1.0, (1.0 - (counter / gridLength)));
            // StdDraw.line(0.0, 0.9, 1.0, 0.9);
            for (int j = 0; j < gridLength; j++) {
                StdDraw.line((1.0 - (counter / gridLength)), 0.0, (1.0 - (counter / gridLength)), 1.0);
            }
        }*/
    }

    /*Node n = new Node(p, (int) p.x(), (int) p.y(), null, null);*/
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("You can not pass a null value as the rectangle parameter");
        if (treeSet.isEmpty()) return null;
        Stack<Point2D> interaPoints = new Stack<>();
        /* Why not add the rectangle coordinates to the SET and use intersects() method between its nodes(minx, miny)
        and (maxx, maxy) and what is already in the tree? intersects() works on two sets, and I do not have two sets;
        just one. And it does not make sense to create a second set made of two points. I do not see it now at least.
        Or ask for all the keys in between these nodes?  */
         double rectMinX = rect.xmin();
         double rectMinY = rect.ymin();
         Point2D minP = new Point2D(rectMinX, rectMinY);
         // RectHV minRect = buildRect(minP);
         double rectMaxX = rect.xmax();
         double rectMaxY = rect.ymax();
        Point2D maxP = new Point2D(rectMaxX, rectMaxY);
        // RectHV maxRect = buildRect(maxP);
        // Node minNode = new Node(minP, minRect);
        // Node minNode = new Node(minP);
        // Node maxNode = new Node(maxP, maxRect);
        // Node maxNode = new Node(maxP);
        for (Iterator<Point2D> it = treeSet.iterator(); it.hasNext(); ) {
            // Node n = it.next();
            point=it.next();
            if ((point.compareTo(maxP) <= 0) && (point.compareTo(minP) >= 0) && (rect.contains(point)))
                interaPoints.push(point);
            // if ((n.p.compareTo(maxP) <= 0) && (n.p.compareTo(minP) >= 0) && (rect.contains(n.p))) interaPoints.push(n.p);
            // if ((n.p.x() >= rectMinX) && (n.p.y() >= rectMinY)&&(n.p.x()<=rectMaxX)&&(n.p.y()<=rectMaxY)) interaPoints.push(n.p);
            /* If you get redundant points replace the stack with something that you can check to see if the point
             * is already in there like ArrayList or something */
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
        // pSet.draw();
        RectHV r = new RectHV(0.0, 0.0, 0.8, 0.8125);
        StdOut.println("Here are the points in rectangle " + r.toString());
        for (Point2D p : pSet.range(r)) {
            StdOut.println(p);
        }
        // Point2D p = new Point2D(StdRandom.uniform(0.0,1.0),StdRandom.uniform(0.0,1.0));
        Point2D p2 = new Point2D(1.0, 0.5);
        StdOut.println(pSet.nearest(p2));
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
