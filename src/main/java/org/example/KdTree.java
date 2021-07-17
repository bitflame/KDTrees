package org.example;

import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.BST;
import java.util.ArrayList;
import java.util.Comparator;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private Node root;
    Queue<Point2D> queue = new Queue<Point2D>();
    private Queue<Node> q = new Queue<>();
    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private MinPQ<Double> xCoordinates = new MinPQ<>();
    IntervalST<Double, Double> intervalSearchTree = new IntervalST();

    private class IntervalST<Key extends Comparable<Key>, Value> extends BST {
        Node root;
        Queue<Value> q = new Queue<>();

        private class Node {
            Key lo;
            Key hi;
            Key maximum;
            Value val;
            Node left, right;
            int N;

            public Node(Key low, Key high, Value value,int N) {
                lo = low;
                hi = high;
                val = value;
                this.N=N;
            }
        }

        void delete(Key low, Key high) {
            delete(low);
        }

        void put(Key low, Key high, Value value) {
// val is the max value
            if (low == null) throw new IllegalArgumentException("calls put(0 with a null key");
            if (value == null) {
                delete(low);
                return;
            }
            root = put(root, low, high, value);
        }

        private Node put(Node h, Key low, Key high, Value value) {
            if (h == null) {
                Node n = new Node(low, high, value,1);
                n.maximum = high;
                return n;
            }
            int cmp = low.compareTo(h.lo);
            if (cmp < 0) {
                h.left = put(h.left, low, high, value);
                //h.left = new Node(low, high, value);
                h.left.maximum=high;
                h.maximum = (h.maximum.compareTo(high) > 0) ? h.maximum : high;

            } else if (cmp > 0) {
                h.right = put(h.right, low, high, value);
                //h.right = new Node(low, high, value);
                h.right.maximum=high;
                h.maximum = (h.maximum.compareTo(high) > 0) ? h.maximum : high;
            } else h.val = value;
            h.N = 1 + size(h.left) + size(h.right);
            return h;
        }

        Key getMaximum() {
            return root.maximum;
        }

        @Override
        public int size() {
            return size(root);
        }

        private int size(Node x) {
            if (x == null) return 0;
            else return x.N;
        }

        Iterable<Value> intersects(Key low, Key high) {
            intersects(root, low, high);
            return q;
        }

        private Node intersects(Node h, Key low, Key high) {
            while (h != null) {
                if (((h.lo.compareTo(low) <= 0 && (h.hi.compareTo(low) >= 0))) ||
                        ((h.hi.compareTo(high) >= 0 && h.lo.compareTo(low) >= 0)) ||
                        ((h.lo.compareTo(low) >= 0) && (h.hi.compareTo(high) <= 0)) || ((h.lo.compareTo(low) <= 0) &&
                        (h.hi.compareTo(high) >= 0))) q.enqueue(h.val);
                if (h.left == null) {
                    h.right = intersects(h.right, low, high);
                    return h.right;
                }
                if (h.left.maximum.compareTo(low) < 0) {
                    h.right = intersects(h.right, low, high);
                    return h.right;
                }
                h.left = intersects(h.left, low, high);
                h.right = intersects(h.right, low, high);
            }
            return h;
        }
    }

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right, parent; // subtrees
        int N; // # nodes in this subtree
        boolean orientation; // 0 means horizontal
        RectHV nodeRect;
        double xCoord;
        double yCoord;
        // maximum values in each tree
        double maximumX = 0.0;
        double maximumY = 0.0;
        // for tracking rectangle intervals
        double minXInter = 0.0;
        double maxXInter = 1.0;
        double minYInter = 0.0;
        double maxYInter = 1.0;

        public Node(Point2D p, int n, boolean coordinate, Node parent) {
            this.p = p;
            this.orientation = coordinate;
            this.parent = parent;
            this.N = n;
            this.nodeRect = null;
        }

        @Override
        public int compareTo(Node h) {
            double thisX = this.p.x();
            double thisY = this.p.y();
            double hX = h.p.x();
            double hY = h.p.y();
            if (!this.orientation) {
                if (thisX < hX) {
                    return -1;
                } else {
                    return 1;
                }
            }
            if (this.orientation) {
                if (thisY < hY) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 1;
        }
    }

    public void draw() {
        RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);
        if (root == null) return;
        draw(root, rec);
    }

    private void draw(Node h, RectHV rectHV) {
        RectHV tempRect;
        if (!h.orientation) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.p.x(), rectHV.ymin(), h.p.x(), rectHV.ymax());
            if (h.left != null) {
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), h.p.x(), rectHV.ymax());
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(h.p.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
            }
        } else if (h.orientation) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.p.x(), h.p.y());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), h.p.y(), rectHV.xmax(), h.p.y());
            if (h.left != null) {
                // the sub rectangles are different depending on parent axis orientation
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), h.p.y());
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(rectHV.xmin(), h.p.y(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
            }
        }

    }

    private Point2D get(Point2D p) {
        return get(root, p);
    }

    public boolean isEmpty() {
        return keys() == null;
    }

    private Point2D get(Node h, Point2D p) {
        if (h == null) return null;
        int cmp = p.compareTo(h.p);
        if (cmp < 0) return get(h.left, h.p);
        else if (cmp > 0) return get(h.right, h.p);
        else return h.p;
    }

    private Iterable<Node> keys() {
        q = new Queue<>();
        return keys(root);
    }

    private Queue<Node> keys(Node h) {
        if (h == null) return null;
        if (h != null) q.enqueue(h);
        if (h.left != null) {
            keys(h.left);
        }
        if (h.right != null) {
            keys(h.right);
        }
        return q;
    }

    private Iterable<Point2D> keys(Point2D lo, Point2D hi) {
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Point2D> queue, Point2D lo, Point2D hi) {
        if (x == null) return;
        int cmplo = lo.compareTo(x.p);
        int cmphi = hi.compareTo(x.p);
        if (cmplo < 0) keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >=0) queue.enqueue(x.p);
        if (cmphi > 0) keys(x.right, queue, lo, hi);
    }

    private Node select(int k) {
        return select(root, k);
    }

    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    private int rank(Node lo) {
        return rank(lo, root);
    }

    private int rank(Node lo, Node x) {
        if (x == null) return 0;
        int cmp = lo.compareTo(x);
        if (cmp < 0) return rank(lo, x.left);
        else if (cmp > 0) return 1 + size(x.left) + rank(lo, x.right);
        else return size(x.left);
    }

    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    private Node floor(Point2D p) {
        return floor(root, p);
    }

    private Node floor(Node x, Point2D point) {
        if (x == null) return null;
        int cmp = x.p.compareTo(point);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, point);
        Node t = floor(x.right, point);
        if (t != null) return t;
        else return x;
    }
    public Iterable<Point2D> range(RectHV rect) {

        if (rect == null) throw new IllegalArgumentException("rectangle has to be a valid " +
                "object. ");
        range(root, rect);
        return points;
    }

    private Iterable<Point2D> range(Node h, RectHV rect) {
        /* Maybe interval search refers to the rectangle's interval i.e. first you find all the rectangles that intersect
        with rect, then you do an sliding interval search for points that are between its minx,miny, maxx, & maxy */
        double lo = rect.ymin();
        double hi = rect.ymax();
        double currentX;
        while (!xCoordinates.isEmpty()) {
            currentX = xCoordinates.delMin();
            if (currentX >= lo) {
                /* I did not see how this next line would work out until I wrote the code! Kept thinking how to get
                 * the y coordinate if I only had x's and vise versa ! wow */
                intervalSearchTree.put(h.minYInter, h.maxYInter, currentX);
                for (Double d : intervalSearchTree.intersects(lo, hi)) {
                    Point2D start = new Point2D(currentX, lo);
                    Point2D end = new Point2D(currentX, hi);
                    for (Point2D p: keys(start, end)){
                        if ((!points.contains(p)) && rect.contains(p)) points.add(p);
                    }
                }
            } else if (currentX >= hi) {
                intervalSearchTree.delete(h.minYInter, h.maxXInter);
            }
        }
        return points;
    }

    private void setLeftRectIntervals(Node x) {

        RectHV left;
        if (!x.orientation) { // Horizontal node
            // left = new RectHV(x.parent.minXInter, x.parent.minYInter, x.parent.p.x(), x.parent.maxYInter);
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.xCoord;
            x.maxYInter = x.parent.maxYInter;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        } else {
            // vertical node
            // left = new RectHV(x.parent.minXInter, x.parent.minYInter, x.parent.maxXInter, x.parent.p.y());
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.xCoord;
            x.maxYInter = x.yCoord;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        }
    }

    private void setRightRectIntervals(Node x) {
        RectHV right;
        if (!x.orientation) { //
            // horizontal node
            // right = new RectHV(x.parent.p.x(), x.parent.minYInter, x.parent.maxXInter, x.parent.maxYInter);
            x.minXInter = x.xCoord;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.maxYInter;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        } else {
            // vertical node
            // right = new RectHV(x.parent.minXInter, x.parent.p.y(), x.parent.maxXInter, x.parent.maxYInter);
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.yCoord;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.maxYInter;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        }
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("You can not insert null object" +
                "into the tree");
        Node newNode = new Node(p, 1, false, null);
        newNode.maximumX = p.x();
        xCoordinates.insert(p.x());
        newNode.xCoord = p.x();
        newNode.yCoord = p.y();
        root = insert(root, newNode);
    }

    private Node insert(Node h, Node newNode) {
        if (h == null) {
            intervalSearchTree.put(0.0, 1.0, 1.0);
            return newNode;
        } else {
            newNode.orientation = !h.orientation;
            int cmp = h.compareTo(newNode);
            if (cmp < 0) {  // It means root is smaller than the new node
                newNode.parent = h;
                setRightRectIntervals(newNode);
                setLeftRectIntervals(newNode);
                // xCoordinates.insert(newNode.p.x());
                h.right = insert(h.right, newNode);
                h.maximumX = Math.max(h.maximumX, h.right.maximumX);
                h.maximumY = Math.max(h.maximumY, h.right.maximumY);
            } else if (cmp > 0) {  // it means root is larger than the new node
                newNode.parent = h;
                setLeftRectIntervals(newNode);
                setRightRectIntervals(newNode);
                // xCoordinates.insert(newNode.p.x());
                h.left = insert(h.left, newNode);
                h.maximumX = Math.max(h.maximumX, h.left.maximumX);
                h.maximumY = Math.max(h.maximumY, h.left.maximumY);
            }
        }
        int leftN = 0;
        if (h.left != null) {
            leftN = h.left.N;
            h.maximumX = Math.max(h.maximumX, h.left.maximumX);
            h.maximumY = Math.max(h.maximumY, h.left.maximumY);
        }
        int rightN = 0;
        if (h.right != null) {
            rightN = h.right.N;
            h.maximumX = Math.max(h.maximumX, h.right.maximumX);
            h.maximumY = Math.max(h.maximumY, h.right.maximumY);
        }
        h.N = leftN + rightN + 1;
        return h;
    }

    public int size() {
        if (root == null) return 0;
            // else return root.N;
        else return size(root);
    }

    private int size(Node x) {
        return size(x.left) + size(x.right) + 1;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) throw new IllegalArgumentException("The tree is empty.");
        /* if the closest point discovered so far is closer than the distance between the query point and the rectangle
        corresponding to a node, there is no need to explore that node (or its subtrees). */
        RectHV initialRec = new RectHV(0.0, 0.0, 1.0, 1.0);
        Point2D nearestNeig = root.p;
        root.nodeRect = initialRec;
        return nearest(root, p, nearestNeig);
    }

    /* I may have to change this method also, and the fix might very well the fact that we have to go towards the query
     * point first. */
    private Point2D nearest(Node h, Point2D p, Point2D nearstP) {
        RectHV rHl = null;
        RectHV rHr = null;
        if (h == null) return nearstP;
        if (!h.orientation) {
            if (h.parent == null) {
                rHl = new RectHV(0.0, 0.0, h.p.x(), 1.0);
                rHr = new RectHV(h.p.x(), 0.0, 1.0, 1.0);
            } else if (h.parent != null) {
                // I have to rebuild the h rectangle here or save it in the node from previous round.
                // How should I handle points like 0.0,0.5? there is no left rectangle if (h.x() == 0) do what?
                rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.p.x(), h.nodeRect.ymax());
                rHr = new RectHV(h.p.x(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.nodeRect.ymax());
            }
            if (h.left != null) {
                if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                    }
                }
                h.left.parent = h;
                h.left.nodeRect = rHl;
                nearstP = nearest(h.left, p, nearstP);
            }
            if (h.right != null) {
                if (rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.right.p;
                    }
                }
                h.right.parent = h;
                h.right.nodeRect = rHr;
                nearstP = nearest(h.right, p, nearstP);
            }

        }
        if (h.orientation) {
            rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.p.y());
            rHr = new RectHV(h.nodeRect.xmin(), h.p.y(), h.nodeRect.xmax(), h.nodeRect.ymax());
            // rHr = new RectHV(h.p.x(),h.nodeRect.ymin(),h.nodeRect.xmax(),h.nodeRect.ymax());
            if (h.left != null) {
                if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                    }
                }
                h.left.parent = h;
                h.left.nodeRect = rHl;
                nearstP = nearest(h.left, p, nearstP);
            }
            if (h.right != null) {
                if (rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                    if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.right.p;
                    }
                }
            }
        }
        return nearstP;
    }

    private int height(Node root) {
        if (root == null)
            return 0;
        else {
            /* Compute the height of each subtree */
            int lheight = height(root.left);
            int rheight = height(root.right);
            /* use the larger one */
            if (lheight > rheight)
                return (lheight + 1);
            else return (rheight + 1);
        }
    }

    /* print the current level */
    private void printCurrentLevel(Node root, int level) {
        if (root == null) return;
        if (level == 1) StdOut.println(root.p);
        else if (level > 1) {
            printCurrentLevel(root.left, level - 1);
            printCurrentLevel(root.right, level - 1);
        }
    }

    private void ensureOrder(Node root, int level) {
        if (root == null || root.left == null || root.right == null) return;
        if (root.compareTo(root.left) < 0) StdOut.println("Need to fix the tree. " +
                root.left + "is on the left of its parent but it is larger, or comparator is " +
                "messed up");
        if (root.compareTo(root.right) > 0) StdOut.println("Need to fix the tree. " + root +
                "is the parent but it is larger than its right child, or comparator is " +
                "messed up");
        else {
            ensureOrder(root.left, level - 1);
            ensureOrder(root.right, level - 1);
        }
    }

    private void ensureOrder() {
        int h = height(root);
        int i;
        for (i = 1; i <= h; i++) {
            ensureOrder(root, i);
        }
    }

    private void printLevelOrder() {
        int h = height(root);
        int i;
        for (i = 1; i <= h; i++) {
            printCurrentLevel(root, i);
        }
    }


    public static void main(String[] args) {
        /* Test all the files to see if they load ok, and seem to produce the right rectangles and etc. */

        KdTree kdtree = new KdTree();
        // kdtree.testRectangles();
        // kdtree.draw();
        String filename = args[0];
        In in = new In(filename);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        RectHV r = new RectHV(0.0, 0.48, 0.02, 0.52);

//        for (Point2D p : kdtree.range(r)) {
//            StdOut.println(" : " + p);
//        }
        //        int increment = 3;
//        Point2D p = null;
//        for (int i = 0; i < 100; i++) {
//            p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
//            kdtree.insert(p);
//        }
//        RectHV r = new RectHV(0.1, 0.1, 0.8, 0.6);
//        StdOut.println("Rectangle: " + r + "Contains points: " + kdtree.range(r));
        //StdOut.println("Here are the points in the above rectangle: ");
        kdtree.range(r);
        StdOut.println("Here are the points in above rectangle: ");
        for (Point2D p: kdtree.points){
            StdOut.println(p);
        }
//        PointSET brute = new PointSET();
//
//        int counter = 0;
//        for (int i = 0; i < 4; i++) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            Point2D p = new Point2D(x, y);
//            kdtree.insert(p);
//        }
//        for (Node node : kdtree.keys()) {
//            //StdOut.printf("%2.2f%n",node.maximumX);
//            StdOut.println("Here is the point: " + node.p + "Here is its maximum x: " + node.maximumX);
//        }

        // StdOut.println(kdtree.root+""+kdtree.root.maximumX);
//            brute.insert(p);
        // counter++;
        // StdOut.println(counter+" th number was just inserted.");
//            StdOut.println();
//            kdtree.printLevelOrder();


        // kdtree.ensureOrder();
        //RectHV r = new RectHV(0.1, 0.1, 0.5, 0.7);

        // Stopwatch st = new Stopwatch();
        // kdtree.range(r);
        // double rangeElapsedTime = st.elapsedTime();
//        StdOut.println("Here are the points in rectangle " + r);

        // StdOut.println("Kd range() took " + rangeElapsedTime + " seconds.");
        // StdOut.printf("Kd range() took %20.6f%n", rangeElapsedTime);
        // kdtree.draw();
        // StdOut.println("now we are going to test range.");
        //
        // StdOut.println("Rectangle: " + r + "Contains points: " + kdtree.range(r));
//        StdOut.println("Should be 10 " + kdtree.size());
//        StdOut.println("Should be 10 " + brute.size());
//        StdOut.println("Should be false " + kdtree.isEmpty());
//        StdOut.println("Should be false " + brute.isEmpty());
//        kdtree.draw();
//        KdTree kt = new KdTree();
//        Point2D p1 = new Point2D(0.5, 0.25);
//        kt.insert(p1);
//        Point2D p2 = new Point2D(0.0, 0.5);
//        kt.insert(p2);
//        Point2D p3 = new Point2D(0.5, 0.0);
//        kt.insert(p3);
//        Point2D p4 = new Point2D(0.25, 0.0);
//        kt.insert(p4);
//        Point2D p5 = new Point2D(0.0, 1.0);
//        kt.insert(p5);
//        Point2D p6 = new Point2D(1.0, 0.5);
//        kt.insert(p6);
//        Point2D p7 = new Point2D(0.25, 0.0);
//        kt.insert(p7);
//        Point2D p8 = new Point2D(0.0, 0.25);
//        kt.insert(p8);
//        Point2D p9 = new Point2D(0.25, 0.0);
//        kt.insert(p9);
//        Point2D p10 = new Point2D(0.25, 0.5);
//        kt.insert(p10);
        // Point2D queryPoint = new Point2D(0.75, 0.75);
        // kt.draw();
        // StdOut.println("Distance Squared to Query Point: " + kt.nearest(queryPoint).distanceSquaredTo(queryPoint));
        // StdOut.println(kt.nearest(queryPoint));
//        StdOut.println("Changed something for testing.");
        //KdTree k = new KdTree();
//        Queue<Point2D> s = new Queue<>();
//        Point2D p1 = new Point2D(0.7, 0.2);
//        s.enqueue(p1);
//        Point2D p2 = new Point2D(0.5, 0.4);
//        s.enqueue(p2);
//        Point2D p3 = new Point2D(0.2, 0.3);
//        s.enqueue(p3);
//        Point2D p4 = new Point2D(0.4, 0.7);
//        s.enqueue(p4);
//        Point2D p5 = new Point2D(0.9, 0.6);
//        s.enqueue(p5);
//        Point2D p6 = new Point2D(0.1, 0.9);
//        s.enqueue(p6);
//        Point2D p7 = new Point2D(0.2, 0.8);
//        s.enqueue(p7);
//        Point2D p8 = new Point2D(0.3, 0.7);
//        s.enqueue(p8);
//        Point2D p9 = new Point2D(0.4, 0.7);
//        s.enqueue(p9);
//        Point2D p10 = new Point2D(0.9, 0.6);
//        s.enqueue(p10);
//        for (Point2D p : s) {
//            k.insert(p);
//        }
        //Stack<RectHV> recs = new Stack<>();
        //RectHV r = new RectHV(0.8, 0.5, 1.0, 0.7);
        //recs.push(r);

        // recs.push(r);
        // r = new RectHV(0.0, 0.0, 1.0, 1.0);
        // recs.push(r);
        // r = new RectHV(0.7, 0.2, 1.0, 1.0);
        // recs.push(r);
//        for (RectHV rec : recs) {
//            StdOut.println("Rectangle: " + rec + "Contains points: " + k.range(rec));
//        }
        // StdOut.println("Does r contain the first node? " + r.contains(p1));
//        for (Point2D p : k.range(r)) {
//            StdOut.println("Here is the points in above rectangle: " + p);
//        }
//        StdOut.println("Here is the point in your rectangle : " + k.range(r));
//        StdOut.println("Rectangle " + r + "has the following points inside it: " + k.range(r));
//        Point2D p = k.nearest(queryPoint);
//        StdOut.println("Here is the nearest point to 0.75, 0.75: " + p);
//        k.draw();
//        for (int i = 0; i < 20; i++) {
//            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
//            k.insert(p);
//        }
//        StdOut.println("Finished w/o errors.");
//        int index = 1;
//        for (Node n : k.keys()) {
//            if (n.coordinate == true) {
//                StdOut.println(index + "-" + n.p);
//                index++;
//            }
//        }
    }
}

