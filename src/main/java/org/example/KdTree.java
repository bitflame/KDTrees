package org.example;


import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Stopwatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
/* todo -- Build a KdTree with the Key, Value generics and Key being the point, and perhaps the value of
    type node. The implement insert first and make sure it works. */
/*
    todo: Arrays of primitive types usually use 24 bytes of header information ( 16 bytes of a object overhead, 4 bytes
     for the length and 4 bytes for padding plus the memory needed to store the values. An array of objects uses 24 bytes
     of overhead plus 8N for each object; plus the object size
     todo: node memory: 8 bytes for each key and value, 8 * 4 = 32, 4 bytes for N, and 16 bytes of Object overhead,
     plus 2*8=16 bytes for the two left and right objects that are null by default, plus 8 bytes of extra overhead for a
     total of 76 bytes. The rest are as follows: boolean: 1, byte: 1, char: 2, int 4, float 4, long 8, double 8
    */

public class KdTree {

    //public KdTree() throws IOException {
    public KdTree() {
    }

    private class IntervalST<Key extends Comparable<Key>, Value> {
        Node root = null;
        ArrayList<Value> intersections = new ArrayList<>();
        ArrayList<Key> keys = new ArrayList<>();
        private static final boolean RED = true;
        private static final boolean BLACK = false;

        public Node moveRedLeft(Node h) {
            flipColors(h);
            if (h.right != null && isRed(h.right.left)) {
                h.right = rotateRight(h.right);
                h = rotateLeft(h);
                flipColors(h);
            }
            return h;
        }


        public void put(Key lo, Key hi, Value val) {
            if (lo == null) throw new IllegalArgumentException("Key can not be null");
            if (val == null) delete(lo, hi);
            root = put(root, lo, hi, val);
            root.color = BLACK;
        }


        private Node put(Node h, Key lo, Key hi, Value val) {
            if (h == null) {
                return new Node(lo, hi, val, 1, RED);
            }
            int cmp = lo.compareTo(h.lo);
            if (cmp < 0 && h.val != val) {
                h.left = put(h.left, lo, hi, val);
//                if (h.left.branchMax.compareTo(hi) < 0) {
//                    h.left.branchMax = hi;
//                }
            } else if (cmp >= 0 && h.val != val) {
                h.right = put(h.right, lo, hi, val);
//                if (h.right.branchMax.compareTo(hi) < 0) {
//                    h.right.branchMax = hi;
//                }
            } else h.val = val;
            if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
            if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left) && isRed(h.right)) flipColors(h);
            h.N = size(h.left) + size(h.right) + 1;
            return h;
        }

        private Node rotateLeft(Node h) {
            if ((h == null) || h.right == null) return h;
            Node x = h.right;
            h.right = x.left;
            x.left = h;
            x.color = h.color;
            h.color = RED;
            x.N = h.N;
            h.N = 1 + size(h.left) + size(h.right);
            return x;
        }

        private Node rotateRight(Node h) {
            if ((h == null) || h.left == null) return h;
            Node x = h.left;
            h.left = x.right;
            x.right = h;
            x.color = h.color;
            h.color = RED;
            x.N = h.N;
            h.N = 1 + size(h.left) + size(h.right);
            return x;
        }

        private void flipColors(Node h) {
            if (h == null || h.left == null || h.right == null) return;
            // root must have opposite color to its children
            if (((isRed(h) && !isRed(h.left) && !isRed(h.right)) || ((!isRed(h)) && (isRed(h.left)) && (isRed(h.right))))) {
                h.color = !h.color;
                h.left.color = !h.left.color;
                h.right.color = !h.right.color;
            }
        }

        private Node moveRedRight(Node h) {
            flipColors(h);
            if ((h.left != null) && !isRed(h.left.left)) h = rotateRight(h);
            return h;
        }

        public void deleteMax() {
            if (!isRed(root.left) && !isRed(root.right)) root.color = RED;
            root = deleteMax(root);
            if (!isEmpty()) root.color = BLACK;
        }

        private Node deleteMax(Node h) {
            if (isRed(h.left)) h = rotateRight(h);
            if (h.right == null) return null;
            if (!isRed(h.right) && !isRed(h.right.left)) h = moveRedRight(h);
            h.right = deleteMax(h.right);
            return balance(h);
        }

        public boolean contains(Key lo, Key hi) {
            if (lo == null) throw new IllegalArgumentException("Argument to contains() cannot be null");
            return get(lo) != null;
        }

        public Value get(Key lo) {
            if (lo == null) return null;
            return get(root, lo);
        }

        private Value get(Node x, Key lo) {
            if (x == null) return null;
            int cmp = lo.compareTo(x.lo);
            if (cmp < 0) return get(x.left, lo);
            else if (cmp > 0) return get(x.right, lo);
            else return x.val;

        }

        public void delete(Key lo, Key hi) {
            if (!isRed(root.left) && !isRed(root.right)) root.color = RED;
            root = delete(root, lo, hi);
            if (!isEmpty()) root.color = BLACK;
        }

        private Node delete(Node x, Key lo, Key hi) {
            if (x == null) return null;
            int cmp = lo.compareTo(x.lo);
            if (cmp < 0) {
                if ((x.left != null) && !isRed(x.left) && !isRed(x.left.left)) x = moveRedLeft(x);
                x.left = delete(x.left, lo, hi);
            } else {
                if (isRed(x.left)) x = rotateRight(x);
                if (lo.compareTo(x.lo) == 0 && x.right == null) return null;
                if ((x.right != null) && !isRed(x.right) && !isRed(x.right.left)) x = moveRedRight(x);
                if (lo.compareTo(x.lo) == 0) {
                    x.val = get(x.right, min(x.right).lo);
                    x.lo = min(x.right).lo;
                    x.right = deleteMin(x.right);
                } else x.right = delete(x.right, lo, hi);
            }
            // x.N = size(x.left) + size(x.right) + 1;
            return balance(x);
        }

        public boolean isEmpty() {
            return size(root) == 0;
        }

        public void deleteMin() {
            if (isEmpty()) return;
            if (!isRed(root.left) && !isRed(root.right)) root.color = RED;
            root = deleteMin(root);
            if (!isEmpty()) root.color = BLACK;
        }

        private Node deleteMin(Node h) {
            if (h.left == null) return null;
            if (!isRed(h.left) && !isRed(h.left.left)) h = moveRedLeft(h);
            h.left = deleteMin(h.left);
            return balance(h);
        }

        private Node balance(Node h) {
            if (h == null) return null;
            if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
            if (isRed(h.left) && h.left != null && isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left) && isRed(h.right)) flipColors(h);
            h.N = size(h.left) + 1 + size(h.right);
            return h;
        }

        public Key min() {
            if (root == null) throw new NoSuchElementException("Empty binary search tree");
            return min(root).lo;
        }

        public Node min(Node x) {
            if (x.left == null) return x;
            return min(x.left);
        }

        Iterable<Value> intersects(Key lo, Key hi) {
            return intersects(root, lo, hi);
        }

        Iterable<Value> intersects(Node x, Key lo, Key hi) {

            if (x == null) return intersections;
            // if x lo is larger than lo and less than hi
            //if (!((x.hi.compareTo(lo) < 0) || (x.lo.compareTo(hi) > 0))) intersections.add(x.val);
            if ((lo.compareTo(x.lo) >= 0) && (lo.compareTo(x.hi) <= 0)) intersections.add(x.val);
            if ((hi.compareTo(x.hi) <= 0) && (hi.compareTo(x.lo) >= 0)) intersections.add(x.val);
            /*if (((x.lo.compareTo(lo) <= 0) && (x.hi.compareTo(hi) >= 0)) ||
                    ((x.lo.compareTo(lo) >= 0) && (x.lo.compareTo(hi) <= 0)) ||
                    ((x.lo.compareTo(lo) <= 0) && (x.hi.compareTo(lo) >= 0)) ||
                    ((x.lo.compareTo(lo) >= 0) && (x.hi.compareTo(hi) <= 0))) {
                intersections.add(x.val);
            }*/
            //if (x.left != null && x.left.branchMax.compareTo(lo) < 0 && x.right != null) intersects(x.right, lo, hi);
            if (x.left != null) intersects(x.left, lo, hi);
            if (x.right != null) intersects(x.right, lo, hi);
            return intersections;
        }

        public boolean isRed(Node x) {
            if (x == null) return false;
            return x.color == RED;
        }

        public int size() {
            if (root == null) return 0;
            return size(root);
        }

        private int size(Node x) {
            if (x == null) return 0;
            return x.N;
        }

        private Iterable<Key> keys() {
            keys = new ArrayList<>();
            return keys(root);
        }

        private Iterable<Key> keys(Node h) {
            if (h == null) return keys;
            keys.add(h.lo);
            if (h.left != null) keys(h.left);
            if (h.right != null) keys(h.right);
            return keys;
        }

        private class Node {

            Key lo;
            Key hi;
            // Key branchMax;
            Value val;
            private Node left;
            private Node right;
            int N;
            boolean color;
            Node root = null;

            public Node(Key lo, Key hi, Value val, int N, boolean color) {
                this.lo = lo;
                this.hi = hi;
                this.val = val;
                //this.branchMax = hi;
                this.N = N;
                this.color = color;
            }
        }
    }

    private Node root;
    private Queue<Point2D> queue = new Queue<Point2D>();
    private Queue<Node> q = new Queue<>();
    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private MinPQ<Double> xCoordinates = new MinPQ<>();
    private ArrayList<Point2D> intersectingNodes = new ArrayList<>();
    private boolean result = false;
    // private int level = 0;
    private int nodesVisited = 0;
    private IntervalST<Double, Double> ist = new IntervalST<Double, Double>();


//    File myObject = new File("output.txt");
//    FileWriter myWriter = new FileWriter(myObject);

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right;
        int N; // # nodes in this subtree
        int level = 0;
        RectHV nodeRect;
        double xCoord;
        double yCoord;
        double maximX = 0;
        double minXInter = 0.0;
        double maxXInter = 1.0;
        Double minYInter = 0.0;
        Double maxYInter = 1.0;


        public Node(Point2D p, int n, RectHV rect) {
            this.p = p;
            this.N = n;
            this.nodeRect = rect;
        }

        @Override
        public int compareTo(Node h) {
            if (this.xCoord == h.xCoord && this.yCoord == h.yCoord) return 0;
            if (this.level % 2 == 0) {
                if (this.xCoord <= h.xCoord) {
                    return -1;
                }
                if (this.xCoord > h.xCoord) {
                    return 1;
                }
            } else if (this.level % 2 != 0) {
                if (this.yCoord <= h.yCoord) {
                    return -1;
                }
                if (this.yCoord > h.yCoord) {
                    return 1;
                }
            }
            return 0;
        }
    }

    public void draw() {
        // RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);
        if (root == null) return;
        root.minXInter = 0.0;
        root.minYInter = 0.0;
        root.maxXInter = 1.0;
        root.maxYInter = 1.0;
        // draw(root);
        for (Node n : keys()) draw(n);
    }

    private void draw(Node h) {
        buildChildRectangle(h, h.left, h.right);
        // convert the rectangle to
        // RectHV tempRect;
        // StringBuilder sb = new StringBuilder();
        if (h.level % 2 == 0) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            //StdDraw.point(h.xCoord, h.yCoord);
            StdDraw.point(h.xCoord, h.yCoord);
            // sb.append(h.xCoord + " ");
            // sb.append(h.yCoord);
            // StdDraw.text(h.xCoord, h.yCoord, sb.toString());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.xCoord, h.minYInter, h.xCoord, h.maxYInter);
            // if h is horizontal draw h's rectangle
            // StdDraw.setPenRadius(0.005);
        } else if (h.level % 2 != 0) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.xCoord, h.yCoord);
            // sb.append(h.xCoord);
            // sb.append(h.yCoord);
            // StdDraw.text(h.xCoord, h.yCoord, sb.toString());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(h.minXInter, h.yCoord, h.maxXInter, h.yCoord);
            // StdDraw.setPenRadius(0.005);
        }
//        if (isEmpty()) return;
//
//        if (h.left != null) {
//            draw(h.left);
//        }
//        if (h.right != null) {
//            draw(h.right);
//        }
    }

    private void drawRectangle(Node h) {
        StdDraw.line(h.minXInter, h.minYInter, h.maxXInter, h.minYInter);
        StdDraw.line(h.minXInter, h.minYInter, h.minXInter, h.maxYInter);
        StdDraw.line(h.minXInter, h.maxYInter, h.maxXInter, h.maxYInter);
        StdDraw.line(h.maxXInter, h.minYInter, h.maxXInter, h.maxYInter);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private Iterable<Node> keys() {
        if (root == null) return null;
        q = new Queue<>();
        q.enqueue(root);
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
        if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.p);
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

    private int rank(Point2D pnt) {
        Node n = new Node(pnt, 1, null);
        n.xCoord = pnt.x();
        n.yCoord = pnt.y();
        return rank(root, n);
    }

    private int rank(Node x, Node n) {
        if (x == null) return 0;
        int cmp = x.compareTo(n);
        if (cmp > 0) return rank(x.left, n);
        else if (cmp < 0) return 1 + size(x.left) + rank(x.right, n);
        else return size(x.left);
    }

    private Point2D get(Point2D point) {
        return get(root, point);
    }

    private Point2D get(Node x, Point2D point) {
        if (x == null) return null;
        Node n = new Node(point, 1, null);
        n.xCoord = point.x();
        n.yCoord = point.y();
        int cmp = x.compareTo(n);
        if (cmp < 0) return get(x.right, point);
        else if (cmp > 0) return get(x.left, point);
        else return x.p;
    }

    public boolean contains(Point2D point) {
        result = false;
        if (point.equals(null)) throw new IllegalArgumentException("You have to pass a valid point object");
        if (isEmpty()) return false;
        if (get(point) == null) return false;
        if (get(point).equals(point)) return true;
        else return false;
    }

    private boolean pointIsInsideRectangle(double px, double py, double minx, double miny, double maxx, double maxy) {
        if (minx <= +px && maxx >= px && miny <= py && maxy >= py) return true;
        return false;
    }

    private Node ceiling(Point2D p) {
        Node h = new Node(p, 1, null);
        Node x = ceiling(root, h);
        if (x == null) return null;
        return x;
    }

    private Node ceiling(Node x, Node h) {
        if (x == null) return null;
        int cmp = x.compareTo(h);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, h);
        Node t = ceiling(x.left, h);
        if (t != null) return t;
        else return x;
    }

    private boolean contains(Node h, Node n) {

        if (h == null) {
            return result;
        }
        int cmp = h.compareTo(n);
        if (cmp > 0 && h.left != null && h.left.maximX < n.xCoord) {
            h = h.left;
            if (h.p.equals(n.p)) return result = true;
            else contains(h, n);
        } else if (cmp < 0 && h.right != null && h.right.maximX < n.xCoord) {

            h = h.right;
            if (h.p.equals(n.p)) return result = true;
            else contains(h, n);
            //}
        }
        if (h.p.equals(n.p)) result = true;
        else if (h.left == null && h.right == null) return false;
        return result;
    }

    private Node floor(Point2D p) {
        Node h = new Node(p, 1, null);
        Node x = floor(root, h);
        if (x == null) return null;
        return x;
    }

    private Node floor(Node x, Node h) {
        if (x == null) return null;
        int cmp = x.compareTo(h);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, h);
        Node t = floor(x.right, h);
        if (t != null) return t;
        else return x;
    }

    //public Iterable<Point2D> range(RectHV r) throws IOException {
    public Iterable<Point2D> range(RectHV r) {
        points = new ArrayList<>();
        if (r == null) throw new IllegalArgumentException("rectangle has to be a valid " +
                "object. ");
        else if (isEmpty()) return null;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);


        // findout why floor returns a node and ceiling returns a point.
        // Point2D fromP = ceiling(lowCorner).p;

        // Point2D toP = floor(hiCorner).p;

        /*for (int i = (rank(lowCorner) - 1); i <= rank(hiCorner); i++) {
            if (select(i) != null && select(i).nodeRect.intersects(r)) {
                if (r.contains(select(i).p)) points.add(select(i).p);
            }
        }*/

//        for (int i = rank(fromP); i < rank(toP); i++) {
//            if (select(i) != null && select(i).nodeRect.intersects(r)) {
//                points.add(select(i).p);
//            }
//        }
//        root.minXInter = 0.0;
//        root.minYInter = 0.0;
//        root.maxXInter = 1.0;
//        root.maxYInter = 1.0;
//        for (Node n : keys()) buildChildRectangle(n, n.left, n.right);
//
//        double currentX = 0;
//        double lo = r.ymin();
//        double hi = r.ymax();
//        while (!xCoordinates.isEmpty()) {
//            currentX = xCoordinates.delMin();
//            addRemoveToIntervalSearchTree(currentX);
//            for (Double d : ist.keys()) {
//                System.out.println("ist lo: " + d + "ist hi: " + ist.get(d));
//            }
//            if (currentX >= r.xmin() && currentX <= r.xmax()) {
//                for (Double d : ist.intersects(r.ymin(), r.ymax())) {
        // d is the lo, and the return value of this is the hi. Get all the points with ranks between these two values
        //ist.get(d);
        //temp = new Point2D(currentX, ist.get(d));
        //if (rect.contains(temp) && (!points.contains(temp))) points.add(temp);

        // Double lo = (d < rect.ymin()) ? d : rect.ymin();
        // Double hi = (ist.get(d) > rect.ymax()) ? ist.get(d) : rect.ymax();

//                    hiPoint = new Point2D(currentX, rect.ymin());
//                    loPoint = new Point2D(currentX, rect.ymax());
//                    for (Point2D p : keys(loPoint, hiPoint)) {
//                    temp=p;
//                    if (rect.contains(temp) && (!points.contains(temp))) points.add(temp);
//                    }
        // put in the hi as what gets returned
//                    if (d >= lo && d <= hi) {
//                        Point2D point = new Point2D(currentX, d);
//                        if (!points.contains(point) && get(point) != null)
//                            points.add(point);
//                    }
        // points.add(new Point2D(currentX, ist.get(d)));
//                    temp = loPoint;
//                    while (!temp.equals(hiPoint)) {
//                        temp = ceiling(loPoint);
//                        points.add(temp);
//                    }
//                    for (int i = rank(hiPoint); i >= rank(loPoint); i--) {
//                        if (select(i) != null) {
//                            temp = select(i).p;
//                            if (!points.contains(temp) && rect.contains(temp)) points.add(temp);
//                        }
//
//                    }
        // }
        //}
        //}
        //myWriter.close();
        return range(root, r);
    }

    private Iterable<Point2D> range(Node x, RectHV r) {

        buildChildRectangle(x, x.left, x.right);
        Point2D lowCorner = new Point2D(r.xmin(), r.ymin());
        Point2D hiCorner = new Point2D(r.xmax(), r.ymax());
        Point2D currentNodePoint = x.p;
        int nodeRank = rank(currentNodePoint);
        if (nodeRank <= rank(hiCorner) && nodeRank >= (rank(lowCorner)-1)) {
            if (r.contains(currentNodePoint))
            points.add(currentNodePoint);
        }
        if (r.intersects(x.nodeRect)) {
            if (x.left != null && x.left.maximX>r.xmin()) range(x.left, r);
            if (x.right != null) range(x.right, r);
        }
        return points;
    }

    //todo: remove filewriter and throws IOException from this method and its overloaded version below before you submit
    // again. Also remove it from main.
    private void addRemoveToIntervalSearchTree(Double currentX) {
        addRemoveToIntervalSearchTree(root, currentX);
    }


    private void addRemoveToIntervalSearchTree(Node h, Double currentX) {
        if (h == null) return;
        if ((currentX >= h.minXInter) && ((ist.get(h.minYInter) == null) || (!ist.get(h.minYInter).equals(h.yCoord)))) {
            ist.put(h.minYInter, h.maxYInter, h.yCoord);
            //StdOut.println("for point" + h.p + "miny is: " + h.minYInter + "maxy is: " + h.maxYInter);
            // myWriter.write("adding " + h.minYInter + ", " + h.maxYInter + ", " + h.yCoord + "\n");
        }
        if (currentX > h.maxXInter) {
            ist.delete(h.minYInter, h.maxYInter);
            // myWriter.write("removing " + h.minYInter + ", " + h.maxYInter + "\n");
        }
        addRemoveToIntervalSearchTree(h.right, currentX);
        addRemoveToIntervalSearchTree(h.left, currentX);
    }


    // build intersects() for this tree and try to use it for range
    private void buildChildRectangle(Node parent, Node leftChild, Node rightChild) {
        if (parent.level % 2 != 0) {
            //RectHV left = new RectHV(parent.minXInter, parent.minYInter, parent.xCoord, parent.maxYInter);

            // if (parent.left != null) parent.left.nodeRect = left;
            if (leftChild != null && parent.left != null) {
                //leftChild.nodeRect = new RectHV(parent.minXInter, parent.minYInter, parent.xCoord, parent.maxYInter);
                leftChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.nodeRect.ymin(), parent.nodeRect.xmax(),
                        parent.yCoord);
//                parent.left.minXInter = parent.minXInter;
//                parent.left.minYInter = parent.minYInter;
//                parent.left.maxXInter = parent.xCoord;
//                parent.left.maxYInter = parent.maxYInter;
//                leftChild.minXInter = parent.minXInter;
//                leftChild.minYInter = parent.minYInter;
//                leftChild.maxXInter = parent.maxXInter;
//                leftChild.maxYInter = parent.yCoord;
                // assert rightChild.maxYInter <= 1.0 : parent.p;
                //assert leftChild.maxXInter <= 1.0 : parent.p;
            }

            // if (parent.right != null) parent.right.nodeRect = right;
            if (rightChild != null && parent.right != null) {
                //rightChild.nodeRect = new RectHV(parent.xCoord, parent.minYInter, parent.maxXInter, parent.maxYInter);
                rightChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.yCoord, parent.nodeRect.xmax(),
                        parent.nodeRect.ymax());
//                parent.right.minXInter = parent.right.xCoord;
//                parent.right.minYInter = parent.minYInter;
//                parent.right.maxXInter = parent.maxXInter;
//                parent.right.maxYInter = parent.maxYInter;
//                rightChild.minXInter = parent.minXInter;
//                rightChild.minYInter = parent.yCoord;
//                rightChild.maxXInter = parent.maxXInter;
//                rightChild.maxYInter = parent.maxYInter;
//                assert rightChild.maxYInter <= 1.0 : parent.p;
//                assert rightChild.maxXInter <= 1.0 : parent.p;
            }
        } else if (parent.level % 2 == 0) {

            // if (parent.left != null) parent.left.nodeRect = left;
            if (leftChild != null && parent.left != null) {
                // leftChild.nodeRect = new RectHV(parent.minXInter, parent.minYInter, parent.maxXInter, parent.yCoord);
                leftChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.nodeRect.ymin(), parent.xCoord,
                        parent.nodeRect.ymax());
//                parent.left.minXInter = parent.minXInter;
//                parent.left.minYInter = parent.minYInter;
//                parent.left.maxXInter = parent.maxXInter;
//                parent.left.maxYInter = parent.left.yCoord;
//                leftChild.minXInter = parent.minXInter;
//                leftChild.minYInter = parent.minYInter;
//                leftChild.maxXInter = parent.xCoord;
//                leftChild.maxYInter = parent.maxYInter;
//                assert leftChild.maxYInter <= 1.0 : parent.p;
//                assert leftChild.maxXInter <= 1.0 : parent.p;
            }

            // if (parent.right != null) parent.right.nodeRect = right;
            if (rightChild != null && parent.right != null) {
                //rightChild.nodeRect = new RectHV(parent.minXInter, parent.yCoord, parent.maxXInter, parent.maxYInter);
                rightChild.nodeRect = rightChild.nodeRect = new RectHV(parent.xCoord, parent.nodeRect.ymin(), parent.nodeRect.xmax(),
                        parent.nodeRect.ymax());
//                parent.right.minXInter = parent.minXInter;
//                parent.right.minYInter = parent.right.yCoord;
//                parent.right.maxXInter = parent.maxXInter;
//                parent.right.maxYInter = parent.maxYInter;
//                rightChild.minXInter = parent.xCoord;
//                rightChild.minYInter = parent.minYInter;
//                rightChild.maxXInter = parent.maxXInter;
//                rightChild.maxYInter = parent.maxYInter;
//                assert rightChild.maxYInter <= 1.0 : parent.p;
//                assert rightChild.maxXInter <= 1.0 : parent.p;
            }
        }
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("You can not insert null object" +
                "into the tree");
        Node n = new Node(p, 1, null);
        n.level = 0;
        n.xCoord = p.x();
        xCoordinates.insert(n.xCoord);
        n.yCoord = p.y();
        n.maximX = n.xCoord;
        root = insert(root, n);
    }

    private Node insert(Node h, Node n) {
        if (h == null) {
            return n;
        }
        int cmp = h.compareTo(n);
        if (cmp < 0) {
            h.right = insert(h.right, n);
            h.maximX = Math.max(h.right.maximX, h.maximX);
            h.right.level = h.level + 1;
        } else if (cmp > 0) {
            h.left = insert(h.left, n);
            h.maximX = Math.max(h.left.maximX, h.maximX);
            h.left.level = h.level + 1;
        } else (h.p) = n.p;
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    public int size() {
        if (root == null) return 0;
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        return x.N;
    }

    private Point2D min() {
        return root.p;
    }

    private Point2D min(Node x) {
        if (x.left == null) return x.p;
        return (min(x.left));
    }

    private Point2D max() {
        return root.p;
    }

    private Point2D max(Node x) {
        if (x.right == null) return x.p;
        return (max(x.right));
    }

    private void print(Node x) {
        if (x == null) return;
        print(x.left);
        System.out.println(x.p);
        print(x.right);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) throw new IllegalArgumentException("The tree is empty.");
        if (contains(p)) return p;
        Point2D nearestNeig = root.p;
        // pointsVisited.add(nearestNeig);
        // nodesVisited++;
        // System.out.println("Tree size : " + root.N);
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return nearest(root, p, nearestNeig);
    }

    private Point2D nearest(Node h, Point2D p, Point2D nearstP) {
        RectHV rHl = null;
        RectHV rHr = null;
        h.xCoord = h.p.x();
        h.yCoord = h.p.y();
        if (h == null) return nearstP;
        if (h.level == 0) { // if (h.parent == null) {
            rHl = new RectHV(0.0, 0.0, h.xCoord, 1.0);
            rHr = new RectHV(h.xCoord, 0.0, 1.0, 1.0);
        } else if (h.level > 0) { // if (h.parent != null) {
            if (h.level % 2 == 0) { // if (!h.orientation) {
                rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.xCoord, h.nodeRect.ymax());
                rHr = new RectHV(h.xCoord, h.nodeRect.ymin(), h.nodeRect.xmax(), h.nodeRect.ymax());
            } else if (h.level % 2 != 0) { // } else if (h.orientation) {
                rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.yCoord);
                rHr = new RectHV(h.nodeRect.xmin(), h.yCoord, h.nodeRect.xmax(), h.nodeRect.ymax());
            }
        }
        if (!rHl.contains(p) || !(rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            if (h.left != null) {
                h.left.nodeRect = rHl;
                // h.left.parent = h;
            }

            if (h.right != null && rHr.contains(p)) {
                h.right.nodeRect = rHr;
                h = h.right;
                nearstP = nearest(h, p, nearstP);
            }
        } else if (!rHr.contains(p) && !(rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            h.right.nodeRect = rHr;
            // h.right.parent = h;
            if (h.left != null) {
                h.left.nodeRect = rHl;
                h = h.left;
                nearstP = nearest(h, p, nearstP);
            }
        } else if (rHl.contains(p) && (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            // check rHl for points
            if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                if (h.left != null) {
                    // nodesVisited++;
                    // System.out.println("Tree size : " + h.N);
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                        // pointsVisited.add(nearstP);
                    }
                    // h.left.parent = h;
                    if (h.right != null) {
                        // h.right.parent = h;
                        h.right.nodeRect = rHr;
                    }
                    h.left.nodeRect = rHl;
                    nearstP = nearest(h.left, p, nearstP);
                    // nearstP = nearest(h.right, p, nearstP);
                }
            }
        } else if (rHr.contains(p) && (rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            // check rHr

            if (h.right != null) {
                // nodesVisited++;
                // System.out.println("Tree size : " + h.N);
                if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                    nearstP = h.right.p;
                    // pointsVisited.add(nearstP);
                }
                // h.right.parent = h;
                // h.left.parent = h;
                h.left.nodeRect = rHl;
                h.right.nodeRect = rHr;
                nearstP = nearest(h.right, p, nearstP);
                // nearstP = nearest(h.left, p, nearstP);
                // floor and ceiling might help
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

    private void populateTree(KdTree kt, File newFile) {
        try {
            Scanner scanner = new Scanner(newFile);
            while (scanner.hasNext()) {
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();
                Point2D p = new Point2D(x, y);
                kt.insert(p);
                // System.out.println(kt.rank(p));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // public static void main(String[] args) throws IOException {
    public static void main(String[] args) {
       /*
        kdtree.insert(new Point2D(0.7, 0.2));
        kdtree.insert(new Point2D(0.5, 0.4));
        kdtree.insert(new Point2D(0.2, 0.3));
        kdtree.insert(new Point2D(0.4, 0.7));
        kdtree.insert(new Point2D(0.9, 0.6));

        kdtree.draw();*/
        /*todo - test floor (largest key lees than ...) and ceiling(smallest key more than...Populate the
           tree first, then run floor and ceiling for each node and nodes slightly less  and more and make
           sure you get what you are supposed to get
           Also create tests for failed tests of last feedback. The feedback does not seem to have a huge
            issue with you using rect.contains() after making sure rectangles intersect, and you can make
            it moe effecient by creating rectangles one step at a time inside range instead of all at once
            so go with this for now ) */
        File fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\distincts.txt");
        KdTree kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        RectHV r = new RectHV(0.6, 0.32, 0.97, 0.8);
        System.out.println("For file distincts.txt we expect: [0.9,0.6], get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\distinctpoints.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.082, 0.50, 0.084, 0.52);
        System.out.println("For file distinctpoints.txt we expect: [0.083,0.51], get:" + kdtree.range(r));
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.371, 0.496, 0.373, 0.498);
        System.out.println("For file distinctpoints.txt we expect: [0.372,0.497], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.563, 0.412, 0.565, 0.414);
        System.out.println("For file distinctpoints.txt we expect: [0.564,0.413], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.225, 0.576, 0.227, 0.578);
        System.out.println("For file distinctpoints.txt we expect: [0.226,0.577], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.143, 0.178, 0.145, 0.180);
        System.out.println("For file distinctpoints.txt we expect: [0.144,0.179], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.31, 0.707, 0.33, 0.709);
        System.out.println("For file distinctpoints.txt we expect: [0.32,0.708], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.861, 0.824, 0.863, 0.826);
        System.out.println("For file distinctpoints.txt we expect: [0.862,0.825], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.784, 0.724, 0.786, 0.726);
        System.out.println("For file distinctpoints.txt we expect: [0.785,0.725], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.498, 0.207, 0.500, 0.209);
        System.out.println("For file distinctpoints.txt we expect: [(0.499,0.208)], get:" + kdtree.range(r));
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.5, 0.0, 1.0, 1.0);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file distinctpoints.txt we expect: [(0.564, 0.413), (0.5, 0.5), (0.785, 0.725), " +
                "(0.862, 0.825), (1.0, 0.5)], get:\n" + kdtree.range(r));

        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\distinctpoints2.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.5, 0.0, 1.0, 1.0);
        System.out.println("For file distinctpoints2.txt we expect: [(0.5, 0.5), (1.0, 0.5)], get:" + kdtree.range(r));

        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.107, 0.54, 0.46, 0.884);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file 3a.txt we expect: [0.4,0.7], get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.598, 0.035, 0.847, 0.367);
        System.out.println("For file input10.txt we expect: empty , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\3b.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.125, 0.25, 0.5, 0.625);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file 3b.txt we expect: [0.25, 0.5] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\non-degenerate.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.3125, 0.5625, 0.875, 0.9375);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file non-degenerate.txt we expect: [(0.5,0.8125), (0.5625, 0.6875)] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\non-degenerate2.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.1875, 0.15625, 0.4375, 0.6875);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file non-degenerate.txt we expect: [(0.21875, 0.0.375), (0.375,0.21875)] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\3c.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.514, 0.066, 0.972, 0.953);
        // StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file 3c.txt we expect: [(0.564,0.413), (0.785,0.725),(0.862, 0.0.825)] , and get:" +
                kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\circle10000.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        Stopwatch timer = new Stopwatch();
        r = new RectHV(0.50347900390625, 0.2066802978515625, 0.50347900390627,
                0.2066802978515626);
        //StdOut.println("Here is what r looks like: " + r);
        double time = timer.elapsedTime();
        System.out.println("For file circle10000.txt we expect: [(0.50347900390626, 0.2066802978515626)] , and get:" +
                kdtree.range(r) + " and it took: " + time + " milliseconds.");
        // fileName = new File("C:\\Users\\kashk\\IdeaProjects\\Streams\\src\\main\\resources\\kdtests\\input1M.txt");
        // kdtree = new KdTree();
        //kdtree.populateTree(kdtree, fileName);
        //r = new RectHV(0.761520, 0.842538, 0.761522, 0.842540);
        //StdOut.println("Here is what r looks like: " + r);
        // System.out.println("For file input1M.txt we expect: [(0.761521,0.842539)] , and get: "+ kdtree.range(r));

//        System.out.println("High corner: " + kdtree.select(kdtree.rank(new Point2D(0.972, 0.953))));
//        System.out.println("Low corner: " + kdtree.select(kdtree.rank(new Point2D(0.514, 0.066))).p);
//        System.out.println(kdtree.select(kdtree.rank(new Point2D(0.972, 0.953)) -
//                kdtree.rank(new Point2D(0.514, 0.066))).p);
//        System.out.println("Here are the points between the two corners of the rectangle: ");
//        for (int i = kdtree.rank(new Point2D(0.972, 0.953)); i >= kdtree.rank(new Point2D(0.514, 0.066)); i--) {
//            if (kdtree.select(i) != null) System.out.println(kdtree.select(i).p);
//        }


        /*HashMap<Integer, Point2D> actualRange = new HashMap<>();
        int i = 0;
        for (Point2D p : kt.range(r)) {
            actualRange.put(i++, p);
        }
        for (Point2D point : expectedPoints) {
            if (point != null) {
                try {
                    assert (actualRange.containsValue(point) && i == actualRange.size()) : "Expected and actual points are not the same. ";
                } catch (AssertionError assertionError) {
                    StdOut.println("actual and expected range are not equal for rectangle " + r);
                }
            }
        }
        System.out.println("Expected Points are: ");
        for (Point2D point2D : expectedPoints) {
            StdOut.println(point2D);
        }
        StdOut.println("KdTree range returns:");
        for (Point2D point2D : kt.range(r)) {
            StdOut.println(point2D);
        }
        r = new RectHV(0.371, 0.496, 0.373, 0.498);
        System.out.println("Expect: [0.372,0.497], get:" + kt.range(r));*/
        // Point2D p = new Point2D(0.5, 0.5);
        // KdTree kdtree = new KdTree();
        /* for (int i = 0; i < 20; i++) {
            kdtree.insert(p);
        }
        System.out.println(kdtree.size()); */
        //String filename = args[0];
        //In in = new In(filename);
        // System.out.println("isEmpty() should be true. " + kdtree.isEmpty());
        // Stopwatch timer = new Stopwatch();
        //while (!in.isEmpty()) {
        //double x = in.readDouble();
        //double y = in.readDouble();
        //Point2D p = new Point2D(x, y);
        //kt.insert(p);
        // kdtree.size();
        // kdtree.isEmpty();
        //}
//        for (Node n:kdtree.keys()) {
//            System.out.println("The ranks for point "+n.p + "is: "+kdtree.rank(n.p));
//        }
//        System.out.println(kdtree.select(0).p);
//        System.out.println(kdtree.select(1).p);
//        System.out.println(kdtree.select(2).p);
//        System.out.println(kdtree.select(3).p);
//        System.out.println(kdtree.select(4).p);
        // from test1 query rectangle = [0.288, 0.827] x [0.218, 0.819]
        //RectHV r = new RectHV(0.288,0.218,0.827,0.819);
        // kdtree.draw();
        // From Distinct Points file
        //RectHV r = new RectHV(0.082, 0.5, 0.084, 0.52);// passed
        // RectHV r = new RectHV(0.082,0.178,0.145,0.52);
        // RectHV r = new RectHV(0.498, 0.207, 0.500, 0.209); passed
        //RectHV r = new RectHV(0.563, 0.412, 0.565, 0.414); //passed
        // RectHV r = new RectHV(0.225, 0.576, 0.227, 0.578); passed
        // RectHV r = new RectHV(0.143, 0.178, 0.145, 0.18); passed
        // RectHV r = new RectHV(0.31, 0.707, 0.33, 0.709); passed
        // RectHV r = new RectHV(0.416, 0.361, 0.418, 0.363);  passed
        //RectHV r = new RectHV(0.371, 0.496, 0.373, 0.498);
        // RectHV r = new RectHV(0.862, 0.824, 0.864, 0.826);
        // from Circle4.txt
        // RectHV r = new RectHV(0.0, 0.49, 0.1, 0.51); 0.0,0.5 works
        // RectHV r = new RectHV(0.49, 0.99, 0.51, 1.0); 0.5,1.0 works
        // RectHV r = new RectHV(0.49, 0.0, 0.51, 0.01); 0.5,0.0 works
        // RectHV r = new RectHV(0.49, .9, 0.51, 1.0);
        // RectHV r = new RectHV(0.9, .49, 1.0, 0.51);
        // from circle10000.txt
        // 0.052657 0.723349 does not work in 10000.txt file
        // RectHV r = new RectHV(0.052656, 0.723348, 0.052658, 0.723350);
        // RectHV r = new RectHV(0.5, 0.7, 0.6, 0.8);
        // RectHV r = new RectHV(0.938152,0.740876 , 0.938154,0.740878);
        // RectHV r = new RectHV(0.003, 0.5, 0.004, 0.6); 0.003089 0.555492 works with this rectangle
        // RectHV r = new RectHV(0.003, 0.55, 0.004, 0.58); but does not work with this. Either my rectangles are wrong
        // or I need to fix the precision
        // System.out.println("put 1000000 nodes in the tree. ");
        // double time = timer.elapsedTime();
        // System.out.println("It took " + time + "to insert and run size() and isEmpty() for 1M nodes. ");
        // System.out.println("Tree size : " + kdtree.size());
        // System.out.println("isEmpty should be false " + kdtree.isEmpty());
        // kdtree.draw();
        // RectHV r = new RectHV(0.675, 0.1875, 0.9375, 0.5);
        // RectHV r = new RectHV(0.25, 0.0, 0.625, 0.75);
        // RectHV r = new RectHV(0.39, 0.03, 0.72, 0.88);
        // RectHV r = new RectHV(0.175, 0.281, 0.742, 0.97);distinct points rectangle
        // RectHV r = new RectHV(0.479, 0.198, 0.894, 0.676);
        // RectHV r = new RectHV(0.125, 0.25, 0.5, 0.625);
        //RectHV r = new RectHV(0.50347900390625, 0.2066802978515625, 0.50347900390627, 0.2066802978515627);
        // RectHV r = new RectHV(0.052656, 0.723348, 0.052658, 0.72335); from 10000.txt
        // RectHV r = new RectHV(0.0, 0.125, 1.0, 0.25);
        //System.out.println(" rectangle: " + r + " contains the following points: ");
        //for (Point2D p : kdtree.range(r)) System.out.println(p);
        // System.out.println("Here is the size of the tree. " + kdtree.size());
        // System.out.println("Here is the nearest node to 0.81, 0.30: " + kdtree.nearest(new Point2D(0.81, 0.30)));
        // System.out.println("The nearest point should be 0.052657, 0.723349: " + kdtree.nearest(new Point2D(0.052657, 0.723340)));
        // System.out.println("The number of nodes visited is:  " + kdtree.nodesVisited);
        // System.out.println("Here are the points visited to get to the nearest neighbor. ");
        // for (Point2D p : kdtree.pointsVisited) {
        // System.out.println(p);
        // }
/*
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.003089, 0.555492)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.798197, 0.098654)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.764989, 0.075994)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.451070, 0.997600)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.494974, 0.000025)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.052657, 0.723349)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.137895, 0.723349)));

        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.4500000, 0.500000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.4500000, 0.200000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.5000000, 0.200000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.5100000, 0.220000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.5100000, 0.220000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.5090000, 0.200000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.5120000, 0.201000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.4100000, 0.100000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.4100000, 0.110000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.003089, 0.555492)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.798197, 0.098654)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.764989, 0.075994)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.451070, 0.997600)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.494974, 0.000025)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.052657, 0.723349)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.137895, 0.723349)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.4500000, 0.500000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.4500000, 0.200000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.5000000, 0.200000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.5100000, 0.220000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.5100000, 0.220000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.5090000, 0.200000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.5120000, 0.201000)));
        System.out.println("It should be false: " + kdtree.contains(new Point2D(0.4100000, 0.100000)));
        System.out.println("It should be true: " + kdtree.contains(new Point2D(0.761521, 0.842539)));*/
    }
}

