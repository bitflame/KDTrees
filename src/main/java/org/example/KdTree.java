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
import java.util.ArrayList;
import java.util.Scanner;
/* todo -- Build a KdTree with the Key, Value generics and Key being the point, and perhaps the value of
    type node. The implement insert first and make sure it works.
    todo: Arrays of primitive types usually use 24 bytes of header information ( 16 bytes of a object overhead, 4 bytes
     for the length and 4 bytes for padding plus the memory needed to store the values. An array of objects uses 24 bytes
     of overhead plus 8N for each object; plus the object size
     todo: node memory: 8 bytes for each key and value, 8 * 4 = 32, 4 bytes for N, and 16 bytes of Object overhead,
     plus 2*8=16 bytes for the two left and right objects that are null by default, plus 8 bytes of extra overhead for a
     total of 76 bytes. The rest are as follows: boolean: 1, byte: 1, char: 2, int 4, float 4, long 8, double 8
    */

public class KdTree {

    public KdTree() {
    }



    private Node root;
    private Queue<Point2D> queue = new Queue<Point2D>();
    private Queue<Node> q = new Queue<>();
    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private MinPQ<Double> xCoordinates = new MinPQ<>();
    private ArrayList<Point2D> intersectingNodes = new ArrayList<>();
    private boolean result = false;
    // private int level = 0;




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
        double maximY = 0;
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
        RectHV rec = new RectHV(0.0, 0.0, 1.0, 1.0);
        root.nodeRect = rec;
        if (root == null) return;
        draw(root);
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
            // StdDraw.line(h.xCoord, h.minYInter, h.xCoord, h.maxYInter);
            StdDraw.line(h.xCoord, h.nodeRect.ymin(), h.xCoord, h.nodeRect.ymax());
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
            // StdDraw.line(h.minXInter, h.yCoord, h.maxXInter, h.yCoord);
            StdDraw.line(h.nodeRect.xmin(), h.yCoord, h.nodeRect.xmax(), h.yCoord);
            // StdDraw.setPenRadius(0.005);
        }
        if (isEmpty()) return;

        if (h.left != null) {
            draw(h.left);
        }
        if (h.right != null) {
            draw(h.right);
        }
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

    public Iterable<Point2D> range(RectHV r) {
        points = new ArrayList<>();
        if (r == null) throw new IllegalArgumentException("rectangle has to be a valid " +
                "object. ");
        else if (isEmpty()) return null;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return range(root, r);
    }

    private Iterable<Point2D> range(Node x, RectHV r) {
        buildChildRectangle(x, x.left, x.right);
        Point2D currentNodePoint = x.p;
        // System.out.println("visted node " + currentNodePoint);
        double currentXCoord = currentNodePoint.x();
        double currentYCoord = currentNodePoint.y();
        if (r.intersects(x.nodeRect)) {
            if (currentXCoord >= r.xmin() && currentXCoord <= r.xmax() && currentYCoord >=
                    r.ymin() && currentYCoord <= r.ymax()) points.add(currentNodePoint);
        }
        if (x.left != null && r.intersects(x.left.nodeRect)) range(x.left, r);
        if (x.right != null && r.intersects(x.right.nodeRect)) range(x.right, r);
        return points;
    }

    private void buildChildRectangle(Node parent, Node leftChild, Node rightChild) {
        if (parent.level % 2 != 0) {

            if (leftChild != null && parent.left != null) {
                leftChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.nodeRect.ymin(), parent.nodeRect.xmax(),
                        parent.yCoord);

            }

            if (rightChild != null && parent.right != null) {
                rightChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.yCoord, parent.nodeRect.xmax(),
                        parent.nodeRect.ymax());
            }
        } else if (parent.level % 2 == 0) {
            if (leftChild != null && parent.left != null) {

                leftChild.nodeRect = new RectHV(parent.nodeRect.xmin(), parent.nodeRect.ymin(), parent.xCoord,
                        parent.nodeRect.ymax());
//
            }

            if (rightChild != null && parent.right != null) {
                rightChild.nodeRect = rightChild.nodeRect = new RectHV(parent.xCoord, parent.nodeRect.ymin(), parent.nodeRect.xmax(),
                        parent.nodeRect.ymax());
//
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
        n.maximY = n.yCoord;
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
            h.maximY = Math.max(h.right.maximY, h.maximY);
            h.right.level = h.level + 1;
        } else if (cmp > 0) {
            h.left = insert(h.left, n);
            h.maximX = Math.max(h.left.maximX, h.maximX);
            h.maximY = Math.max(h.left.maximY, h.maximY);
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
    public static void main(String[] args) {
        File fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\distincts.txt");
        KdTree kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        kdtree.draw();
        RectHV r = new RectHV(0.6, 0.32, 0.97, 0.8);
        //RectHV r = new RectHV(0.58, 0.68, 0.87, 0.79);
        StdOut.println("Here is what r looks like: " + r);
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
    }
}

