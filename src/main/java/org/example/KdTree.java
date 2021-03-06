package org.example;
/* A very useful link for this project https://www.cis.upenn.edu/~matuszek/cit594-2002/Pages/recursion-c.html */

import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/*
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
    private Point2D nearestPoint = new Point2D(1.0, 1.0);

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right;
        int N; // # nodes in this subtree
        int level = 0;
        RectHV nodeRect;
        double xCoord;
        double yCoord;
        double maximX = 0;

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
        if (root == null) return;
        draw(root, rec);
    }

    private void draw(Node h, RectHV rectHV) {
        RectHV tempRect = null;
        if (h.level % 2 == 1) {
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
        } else if (h.level % 2 == 0) {
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
        if (point == null)
            throw new IllegalArgumentException("you should pass a valid point data to contains() method.");
        return get(point) != null;
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
        double currentXCoord = currentNodePoint.x();
        double currentYCoord = currentNodePoint.y();
        if (currentXCoord >= r.xmin() && currentXCoord <= r.xmax() && currentYCoord >=
                r.ymin() && currentYCoord <= r.ymax()) points.add(currentNodePoint);
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
            }
            if (rightChild != null && parent.right != null) {
                rightChild.nodeRect = new RectHV(parent.xCoord, parent.nodeRect.ymin(), parent.nodeRect.xmax(),
                        parent.nodeRect.ymax());
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

    /* public Point2D nearest(Point2D pt) {
        if (pt == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) return null;
        if (contains(pt)) return pt;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return nearest(root, pt, root.p);
    }

    private Point2D nearest(Node h, Point2D pt, Point2D nearestNeig) {
        if (h == null) return nearestNeig;
        buildChildRectangle(h, h.left, h.right);
        if (h.nodeRect.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
            System.out.println("node " + h.p + " is involved.");
            if (h.left != null && h.right != null && h.left.nodeRect.contains(pt)) {
                if (h.left.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                    nearestNeig = nearest(h.left, pt, h.left.p);
                }
                if (h.right != null && h.right.nodeRect.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                    if (h.right.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                        nearestNeig = nearest(h.right, pt, h.right.p);
                    }
                }
                nearestNeig = nearest(h.right, pt, nearestNeig);
                nearestNeig = nearest(h.left, pt, nearestNeig);
            } else if (h.right != null && h.right.nodeRect.contains(pt)) {
                if (h.right.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                    nearestNeig = nearest(h.right, pt, h.right.p);
                }
                if (h.left != null && h.left.nodeRect.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                    if (h.left.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                        nearestNeig = nearest(h.left, pt, h.left.p);
                    }
                }
                nearestNeig = nearest(h.right, pt, nearestNeig);
                nearestNeig = nearest(h.left, pt, nearestNeig);
            } else if (h.right != null && h.left != null && (!h.right.nodeRect.contains(pt)) &&
                    (!h.left.nodeRect.contains(pt))) {
                if (h.right.nodeRect.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt))
                    if (h.right.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                        nearestNeig = nearest(h.right, pt, h.right.p);
                    } else if (h.left.nodeRect.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt)) {
                        if (h.left.p.distanceSquaredTo(pt) < nearestNeig.distanceSquaredTo(pt))
                            nearestNeig = nearest(h.left, pt, h.left.p);
                    }
            }
        }
        return nearestNeig;
    } */

    public Point2D nearest(Point2D pt) {
        if (pt == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) return null;
        if (contains(pt)) return pt;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        //System.out.println("looking at " + root.p + " node. ");
        Node queryNode = new Node(pt, 1, root.nodeRect);
        queryNode.xCoord = pt.x();
        queryNode.yCoord = pt.y();
        nearestPoint = root.p;
        Node nearestNeigNode = root;
        return nearest(root, queryNode, pt);
    }

    private Point2D nearest(Node h, Node qNode, Point2D pt) {
        if (h.nodeRect.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt)) {
            buildChildRectangle(h, h.left, h.right);
            int cmp = h.compareTo(qNode);
            if (cmp < 0) {
                if (h.right != null) {
                    //System.out.println("looking at " + h.right.p + " node. ");
                    if (h.right.p.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt)) {
                        //nearestNeigNode = h.right;
                        nearestPoint = h.right.p;
                    }
                    nearest(h.right, qNode, pt);
                }
                if (h.left != null) {
                    //System.out.println("looking at " + h.left.p + " node. ");
                    if (h.left.p.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt)) {
                        //nearestNeigNode = h.left;
                        nearestPoint = h.left.p;
                    }
                    //if (rectanglesOverlap(nearestNeigNode, h.left))
                    if (h.left.nodeRect.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt))
                        nearest(h.left, qNode, pt);
                }
            } else if (cmp > 0) {
                if (h.left != null) {
                    //System.out.println("looking at " + h.left.p + " node. ");
                    if (h.left.p.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt)) {
                        //nearestNeigNode = h.left;
                        nearestPoint = h.left.p;
                    }
                    nearest(h.left, qNode, pt);
                }
                if (h.right != null) {
                    //System.out.println("looking at " + h.right.p + " node. ");
                    if (h.right.p.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt)) {
                        //nearestNeigNode = h.right;
                        nearestPoint = h.right.p;
                    }
                    //if (rectanglesOverlap(nearestNeigNode, h.right))
                    if (h.right.nodeRect.distanceSquaredTo(pt) < nearestPoint.distanceSquaredTo(pt))
                        nearest(h.right, qNode, pt);
                }
//                if (h.left != null) nearestNeigNode = nearest(h.left, qNode, nearestNeigNode, pt);
//                if (h.right != null && rectanglesOverlap(nearestNeigNode, h.right))
//                    nearestNeigNode = nearest(h.right, qNode, nearestNeigNode, pt);
            }
        }
        return nearestPoint;
        // if (h.right != null && h.right.xCoord > xLowBound && h.right.yCoord > yLowBound) nearest(h.right, qNode, pt);
        // if (h.left != null && h.left.xCoord < xHighBound && h.left.yCoord < yHighBound) nearest(h.left, qNode, pt);
    }

    private boolean rectanglesOverlap(Node nearestNeigNode, Node m) {
//        if (nearestNeigNode.nodeRect.xmin() >= m.nodeRect.xmax() || m.nodeRect.xmin() >= nearestNeigNode.nodeRect.xmin() ||
//                nearestNeigNode.nodeRect.ymin() >= m.nodeRect.ymax() || m.nodeRect.ymax() >= nearestNeigNode.nodeRect.ymax())
//            return false;
        if (nearestNeigNode.level == m.level) return true;
        else if (nearestNeigNode.nodeRect.xmin() >= m.nodeRect.xmax() || nearestNeigNode.nodeRect.ymin() >= m.nodeRect.ymax())
            return false;
        else return true;
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
        // empty file should result in null returned value from nearest()
        File fileName = new File("src\\main\\resources\\input0.txt");
        KdTree kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        Point2D point = new Point2D(0.5, 0.5);
        // assert kdtree.nearest(point) == null : "Empty file does not return null.";
        assert kdtree.nearest(point) == null : "Empty file does not return null.";
        System.out.println("******************* Test 1 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        // point = new Point2D(0.9, 0.7);
        point = new Point2D(0.82, 0.38);
        //System.out.println("Test 1. For point (0.9,0.7) file 3a. We expect (0.9, 0.6), and get: " + kdtree.nearest(point));
        // System.out.println(" Test#1: File 3a; expecting (0.9, 0.6), getting: " + kdtree.nearest(point));
        System.out.println("For point (0.82,0.38) file 3a, Tree traversal should be: (0.7,0.2),(0.9,0.6),(0.5,0.4), (0.2,0.3),(0.4,0.7)");
        //System.out.println("Test 1. For point (0.82,0.38) file 3a. We expect (0.7, 0.2), and get: " + kdtree.nearest(point));
        // System.out.println("Test 1. For point (0.82,0.38) file 3a, Tree traversal should be: (0.7,0.2),(0.9,0.6), (0.5,0.4),(0.2,0.3),(0.4,0.7)");
        //System.out.println("Test 1. For point (0.82,0.38) file 3a. We expect (0.7, 0.2), and using nearest() we get: " + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.7, 0.2)) : "Test 1 failed.";
        System.out.println("******************* Test 1.1 *****************************");
        point = new Point2D(0.84, 0.39);
        System.out.println("For point (0.82,0.38) file 3a, Tree traversal should be: (0.7,0.2),(0.9,0.6),(0.5,0.4), (0.2,0.3),(0.4,0.7)");
        assert kdtree.nearest(point).equals(new Point2D(0.9, 0.6)) : "Test 1 failed.";
        System.out.println("******************* Test 2 *****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.5, 0.5);
        //System.out.println("Test2. For file 3a. expecting (0.5, 0.4), getting: " + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.5, 0.4)) : "Test 2 failed.";
        // System.out.println(" Test#2:  File 3a; we expect (0.5, 0.4), using nearest() and Query Point (0.5, 0.5) we " get: " + kdtree.nearest(point));
        System.out.println("******************* Test 3 *****************************");
        //fileName = new File("src\\main\\resources\\3a.txt");
        //kdtree = new KdTree();
        //kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.1, 0.1);
        //System.out.println("Test 3. For file 3a. expecting (0.2, 0.3), getting: " + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.2, 0.3)) : "Test 3 failed.";
        // System.out.println(" Test#3:  File 3a; we expect (0.2, 0.3), using nearest() and Query Point: (0.1, 0.1) we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 4 *****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.3, 0.8);
        //System.out.println("Test 4. For file 3a. expecting (0.4, 0.7), getting: " + kdtree.nearest(point));
        // System.out.println(" Test#4:  File 3a; We expect (0.4, 0.7), using nearest() and Query Point (0.3, 0.8) we "get: " + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 4 failed.";
        System.out.println("******************* Test 5*****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.5, 0.8);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 5 failed.";
        //System.out.println("Test#5. For file 3a. expecting (0.4, 0.7), getting: " + kdtree.nearest(point));
//        System.out.println(" Test#5:  File 3a; We expect (0.4, 0.7), using nearest() and Query Point (0.5, 0.8) we " +
//                "get: " + kdtree.nearest(point));
        System.out.println("******************* Test 6 *****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.95, 0.5);
        assert kdtree.nearest(point).equals(new Point2D(0.9, 0.6)) : "Test 6 failed.";
        //System.out.println("Test #6. For file 3a. expecting (0.9, 0.6), getting: " + kdtree.nearest(point));
//        System.out.println(" Test#6:  File 3a; We expect (0.9, 0.6), using nearest() and Query Point (0.95, 0.5) we " +
//                "get: " + kdtree.nearest(point));
        System.out.println("******************* Test 7 *****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.158, 0.553);
        assert kdtree.nearest(point).equals(new Point2D(0.2, 0.3)) : "Test 7 failed.";
        //System.out.println(" Test #7. For file 3a. expecting (0.2, 0.3), getting: " + kdtree.nearest(point));
//        System.out.println(" Test#7:  File 3a; We expect (0.2, 0.3), using nearest() and Query Point (0.158, 0.553) we " +
//                "get: " + kdtree.nearest(point));
        System.out.println("******************* Test 8 *****************************");
        // fileName = new File("src\\main\\resources\\3a.txt");
        // kdtree = new KdTree();
        // kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.78, 0.44);
        assert kdtree.nearest(point).equals(new Point2D(0.9, 0.6)) : "Test 8 failed.";
        // System.out.println(" expecting (0.9, 0.6), and visited nodes should be: (0.7, 0.2) (0.9, 0.6) (0.5, 0.4) (0.4, 0.7), (0.2, 0.3) getting:  " + kdtree.nearest(point));
        // System.out.println(" File 3a; Here is the result of nearest version 2: expecting (0.9, 0.6), and visited nodes should be: " + (0.7, 0.2) (0.9, 0.6) (0.5, 0.4) (0.4, 0.7), (0.2, 0.3) getting: ");
        System.out.println(" File 3a; Here is the result of nearest(): expecting (0.9, 0.6), and visited nodes should be: " +
                "(0.7, 0.2) (0.9, 0.6) (0.5, 0.4) (0.4, 0.7), (0.2, 0.3) getting: ");
        //System.out.println("Test #8. For file 3a. expecting (0.9, 0.6), and getting: " + kdtree.nearest(point));
//        System.out.println(" Test#8:  File 3a; We expect (0.9, 0.6), using nearest() and Query Point (0.78, 0.44) we get: "
//                + kdtree.nearest(point));
        System.out.println("******************* Test 9 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.707, 0.978);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 9 failed.";
        //System.out.println("Test #9. For file 3a. expecting (0.4, 0.7), and getting: " + kdtree.nearest(point));
//        System.out.println(" Test#9:  File 3a; We expect (0.4, 0.7), using nearest() and Query Point (0.707, 0.978) we " +
//                "get: " + kdtree.nearest(point));
        System.out.println("******************* Test 10 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.396, 0.077);
        assert kdtree.nearest(point).equals(new Point2D(0.2, 0.3)) : "Test 10 failed.";
        //System.out.println("Test #10. For file 3a. expecting (0.2, 0.3), and getting: " + kdtree.nearest(point));
//        System.out.println(" Test#10:  File 3a; We expect (0.2, 0.3), using nearest() and Query Point (0.396, 0.077) " +
//                "and we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 11 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.385, 0.073);
        assert kdtree.nearest(point).equals(new Point2D(0.2, 0.3)) : "Test 11 failed.";
        //System.out.println("Test #11. For file 3a. expecting (0.2, 0.3), and getting: " + kdtree.nearest(point));
//        System.out.println(" Test#11:  File 3a; We expect (0.2, 0.3), using nearest() and Query Point (0.385, 0.73) we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 12 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.662, 0.874);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 12 failed.";
        //System.out.println("Test #12: For file 3a. We expect (0.4, 0.7), using nearest() and Query Point ( , ) we get: " + kdtree.nearest(point));
//        System.out.println(" Test#12:  File 3a; We expect (0.4, 0.7), using nearest() and Query Point ( 0.662, 0.874) we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 12.1 *****************************");
        fileName = new File("src\\main\\resources\\3c.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.73, 0.62);
//        System.out.println("Test #12.1: For file 3c. We expect (0.785, 0.725), using nearest() and Query Point " +
//                "( 0.73, 0.62 ) we get: " + kdtree.nearest(point));
        System.out.println(" The visited nodes should be: (0.372, 0.497) (0.564, 0.413) (0.862, 0.825) (0.785, 0.725). ");
        assert kdtree.nearest(point).equals(new Point2D(0.785, 0.725)) : "Test 12.1 failed.";
        System.out.println("******************* Test 13 *****************************");
        fileName = new File("src\\main\\resources\\3d.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.9375, 0.4375);
        assert kdtree.nearest(point).equals(new Point2D(0.75, 0.5)) : "Test 13 failed.";
        //System.out.println("Test#13. For file 3d. We expect (0.75, 0.5), using nearest() and Query Point ( , ) we get: " + kdtree.nearest(point));
//        System.out.println(" Test#13:  File 3a; We expect (0.75, 0.5), using nearest() and Query Point ( 0.9375, 0.4375) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 14 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.224, 0.258);
        assert kdtree.nearest(point).equals(new Point2D(0.2, 0.3)) : "Test 14 failed.";
//        System.out.println("Test #14. For file: 3a. We expect (0.2, 0.3), using nearest() and Query Point ( 0.224, 0.258) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 15 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.09, 0.889);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 15 failed.";
//        System.out.println("Test #15. For file: 3a. We expect (0.4, 0.7), using nearest() and Query Point ( 0.09, 0.889)" +
//                " we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 16 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.02, 0.64);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 16 failed.";
//        System.out.println("The sequence should be: (0.7,0.2), (0.5,0.4), (0.4,0.7), (0.2,0.3).");
//        System.out.println("Test #16. For file: 3a. We expect (0.4, 0.7), using nearest() and Query Point ( 0.02, 0.64) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 17 *****************************");
        fileName = new File("src\\main\\resources\\3e.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.875, 0.25);
        assert kdtree.nearest(point).equals(new Point2D(0.75, 0.625)) : "Test 17 failed.";
        System.out.println("Test #17. The squence of points should be: (0.375, 0.875), (0.75, 0.625), (0.5, 1.0).");
//        System.out.println("Test #17. For file: 3e. We expect (0.75, 0.625), using nearest() and Query Point ( 0.875, 0.25 ) we get: "
//                + kdtree.nearest(point));
        System.out.println("******************* Test 18 *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        // point = new Point2D(0.362, 0.128);
        point = new Point2D(0.98, 0.96);
        assert kdtree.nearest(point).equals(new Point2D(0.862, 0.825)) : "Test 18 failed.";
        //System.out.println("Test #18. For file input10.txt, and Point: 0.362, 0.128. We expect (0.499, 0.208), and get:" +
        //      " " + kdtree.nearest(point));
        System.out.println("The order of nodes visited should be: (0.372, 0.497), (0.564, 0.413), (0.862, 0.862), (0.785,0.725)");
        // System.out.println("Test #18. For file input10.txt, and Point: 0.98, 0.96. We expect (0.862, 0.825), and get:" + kdtree.nearest(point));
        // System.out.println(" Test#18:  File input10. We expect (0.862, 0.825), using nearest() and Query Point ( 0.98, 0.96) we get: " + kdtree.nearest(point));
        // Point2D ExistingPoint = new Point2D(0.785, 0.725);
        // StdOut.println("Testing get(). Expecting null, using nearest() and Query Point ( , ) we get: " + kdtree.get(point));
        // StdOut.println("Testing get(). Expecting (0.785, 0.725), using nearest() and Query Point ( , ) we get: " + kdtree.get(ExistingPoint));
        System.out.println("******************* Test 19  *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.671, 0.077);
        assert kdtree.nearest(point).equals(new Point2D(0.499, 0.208)) : "Test 19 failed.";
        // System.out.println("Test #19. For file input10. We expect (0.499, 0.208), using nearest() and Query Point ( , ) we get: " + kdtree.nearest(point));
//        System.out.println(" Test#19:  File input10. We expect (0.499, 0.208), using nearest() and Query Point ( 0.671, 0.077 ) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 20 *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.759, 0.543);
        assert kdtree.nearest(point).equals(new Point2D(0.785, 0.725)) : "Test 20 failed.";
        //System.out.println("Test #20. For file input10. We expect (0.785, 0.725), using nearest() and Query Point ( , ) we get: " + kdtree.nearest(point));
//        System.out.println(" Test#20:  File input10. We expect (0.785, 0.725), using nearest() and Query Point (0.759, 0.543 )" +
//                " we get: " + kdtree.nearest(point));
        System.out.println("*******************Test 21 *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.315, 0.09);
        assert kdtree.nearest(point).equals(new Point2D(0.144, 0.179)) : "Test 21 failed.";
        //System.out.println("Test #21. For file input10. We expect (0.144, 0.179), using nearest() and Query Point ( , ) we get: " + kdtree.nearest(point));
//        System.out.println(" Test#21:  File input10; We expect (0.144, 0.179), using nearest() and Query Point (0.315, 0.09) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 22 *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.005, 0.649);
        assert kdtree.nearest(point).equals(new Point2D(0.083, 0.51)) : "Test 22 failed.";
//        System.out.println("Test#22. For file:input10.txt We expect (0.083, 0.51), using nearest() and Query Point ( 0.005, 0.649 ) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 23 *****************************");
        fileName = new File("src\\main\\resources\\3f.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.25, 0.75);
        assert kdtree.nearest(point).equals(new Point2D(0.375, 0.625)) : "Test 23 failed.";
        //System.out.println("Test 23. For point (0.9,0.7) file 3a. We expect (0.9, 0.6), and get: " + kdtree.nearest(point));
        //System.out.println("Test 23. For point (0.25,0.75) file 3f. We expect (0.375, 0.625), and get: " + kdtree.nearest(point));
        System.out.println("For point (0.25,0.75) file 3f, Tree traversal should be: (0.0,0.5), (0.375,0.625), (0.875,0.875)," +
                "(0.625,1.0),(0.125,0.25)");
        System.out.println("******************* Test 24 *****************************");
        fileName = new File("src\\main\\resources\\3g.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.6175, 0.1775);
        assert kdtree.nearest(point).equals(new Point2D(0.625, 0.3125)) : "Test 24 failed.";
        //System.out.println("Test 24. For point (0.9,0.7) file 3a. We expect (0.9, 0.6), and get: " + kdtree.nearest(point));
        System.out.println("Test 24.  For point (0.6175,0.1775) file 3g, Tree traversal should be: (0.0, 0.0), (0.75, 0.8125) " +
                ",(0.5, 0.375),(0.875, 0.4375),(0.9375, 0.25),(0.625, 0.3125)");
        //System.out.println("Test 24. For point (0.6175,0.1775) file 3g. We expect (0.625, 0.3125), and get: " kdtree.nearest(point));
        System.out.println("******************* Test 25 *****************************");
        fileName = new File("src\\main\\resources\\input10.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.279, 0.302);
        // assert kdtree.nearest(point).equals(new Point2D(0.417, 0.362)) : "Test 25 failed.";
        System.out.println("Test#25. For point (0.279, 0.302) file:input10.txt We expect (0.417, 0.362), using nearest() " +
                "and Query Point ( 0.279, 0.302) we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 26 *****************************");
        fileName = new File("src\\main\\resources\\input20.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.25, 0.03125);
        //System.out.println("Test#26. For file:input20.txt We expect (0.375, 0.0625), using nearest() and Query Point (0.25, 0.03125) we get: " + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.375, 0.0625)) : "Test 26 failed.";
        System.out.println("******************* Test 27 *****************************");
        fileName = new File("src\\main\\resources\\input20-b.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.5, 0.0);
        assert kdtree.nearest(point).equals(new Point2D(0.4375, 0.25)) : "Test 27 failed.";
//        System.out.println("Test#27. For file:input20-b.txt We expect (0.4375, 0.25), using nearest() and Query Point ( 0.5, 0.0 ) " +
//                "we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 28 *****************************");
        fileName = new File("src\\main\\resources\\3a.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.12, 0.85);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 28 failed.";
        System.out.println("Test #28.  For point (0.12, 0.85) file 3a, Tree traversal should be: (0.7, 0.2), (0.5, 0.4) ,(0.4, 0.7)");
        //System.out.println("Test #28. For point (0.12, 0.85) file:3a.txt We expect (0.4, 0.7), using nearest() and Query Point (0.12, 0.85 ) we get: " + kdtree.nearest(point));
        System.out.println("******************* Test 29 *****************************");
        fileName = new File("src\\main\\resources\\input0.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.12, 0.85);
        fileName = new File("src\\main\\resources\\input1.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.12, 0.85);
        fileName = new File("src\\main\\resources\\input5.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.704, 0.978);
        assert kdtree.nearest(point).equals(new Point2D(0.4, 0.7)) : "Test 29 failed.";
        //System.out.println("Test #29.  For point (0.704, 0.978) file 3a. We expect: (0.4, 0.7). We get: " + kdtree.nearest(point));
        System.out.println("******************* Test 30 *****************************");
        fileName = new File("src\\main\\resources\\circle4.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.0, 0.5);
        //System.out.println("Test #29.  For point (0.0, 0.5) file circle4.txt, we get:" + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.0, 0.5)) : "Test 30 failed.";
        point = new Point2D(0.5, 1.0);
        //System.out.println("Test #30.  For point (0.5, 1.0) file circle4.txt, we get:" + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.5, 1.0)) : "Test 30 failed.";
        point = new Point2D(0.5, 0.0);
        //System.out.println("Test #31.  For point (0.5, 0.0) file circle4.txt, we get:" + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.5, 0.0)) : "Test 30 failed.";
        point = new Point2D(0.1, 0.6);
        //System.out.println("Test #32.  For point (0.1, 0.6) file circle4.txt, we get:" + kdtree.nearest(point));
        assert kdtree.nearest(point).equals(new Point2D(0.0, 0.5)) : "Test 30 failed.";
        System.out.println("******************* Test 31 *****************************");
        fileName = new File("src\\main\\resources\\3h.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.84375, 0.78125);
        assert kdtree.nearest(point).equals(new Point2D(0.78125, 1.0)) : "Test 31 failed.";
//        System.out.println("Test #31.  For point (0.84375, 0.78125) file 3h. We expect: (0.78125, 1.0). We get: " +
//                kdtree.nearest(point));
        System.out.println("The sequence of points should be: (0.40625, 0.53125) (0.53125, 0.343750) (0.625, 0.65625)" +
                "(0.78125, 1.0) (0.96875, 0.4375) (0.4375, 0.8125) (0.5, 0.71875) (0.5625, 0.9375)");
        System.out.println("******************* Test 32 *****************************");
        fileName = new File("src\\main\\resources\\input20.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.490234375, 0.568359375);
        System.out.println(" Test#31: File circle100; expecting (0.40625, 0.5), getting: " + kdtree.nearest(point));
        // assert kdtree.nearest(point).equals(new Point2D(0.980147, 0.639496)) : "Test 32 failed.";
        // System.out.println(" Test#1: File 3a; expecting (0.9, 0.6), getting: " + kdtree.nearest(point));
        // System.out.println(" Test#18:  File 3a;  expecting (0.9, 0.6), and getting: " + kdtree.nearest(point));
        // System.out.println(" Test#18:  File 3a;  expecting (0.4, 0.7), and getting: " + kdtree.nearest(point));
        // System.out.println("expecting (0.4, 0.7), and getting: " + kdtree.nearest(point));
        // System.out.println(" visited nodes should be: (0.7, 0.2) (0.5, 0.4) (0.4, 0.7). ");
        // System.out.println("expecting (0.9, 0.6), and getting: " + kdtree.nearest(point));
        /*System.out.println("*******************next test*****************************");
        fileName = new File("src\\main\\resources\\input200K.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        point = new Point2D(0.203125, 0.59375);
        Point2D e = new Point2D(0.3125, 0.578125);
        System.out.println("expecting (0.3125, 0.578125), and getting: " + kdtree.nearest(point));
        System.out.println("Distance to actual value: " + point.distanceSquaredTo(kdtree.nearest(point)));
        StdOut.println("Distance to actual value: " + point.distanceSquaredTo(kdtree.nearest(point)));
        System.out.println("Distance to expected value: " + e.distanceSquaredTo(point));
         All these tests for range() passed
        File fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\distincts.txt");
        KdTree kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        kdtree.draw();
        RectHV r = new RectHV(0.6, 0.32, 0.97, 0.8);
        // RectHV r = new RectHV(0.58, 0.68, 0.87, 0.79);
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
        StdOut.println("Here is what r looks like: " + r);
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
        StdOut.println("Here is what r looks like: " + r);
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
        StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file 3b.txt we expect: [0.25, 0.5] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\non-degenerate.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.3125, 0.5625, 0.875, 0.9375);
        StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file non-degenerate.txt we expect: [(0.5,0.8125), (0.5625, 0.6875)] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\non-degenerate2.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.1875, 0.15625, 0.4375, 0.6875);
        StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file non-degenerate.txt we expect: [(0.21875, 0.0.375), (0.375,0.21875)] , get:" + kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\3c.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        r = new RectHV(0.514, 0.066, 0.972, 0.953);
        StdOut.println("Here is what r looks like: " + r);
        System.out.println("For file 3c.txt we expect: [(0.564,0.413), (0.785,0.725),(0.862, 0.0.825)] , and get:" +
                kdtree.range(r));
        fileName = new File("C:\\Users\\kashk\\IdeaProjects\\KDTrees\\src\\main\\resources\\kdtests\\circle10000.txt");
        kdtree = new KdTree();
        kdtree.populateTree(kdtree, fileName);
        Stopwatch timer = new Stopwatch();
        r = new RectHV(0.50347900390625, 0.2066802978515625, 0.50347900390627,
                0.2066802978515626);
        StdOut.println("Here is what r looks like: " + r);
        double time = timer.elapsedTime();
        System.out.println("For file circle10000.txt we expect: [(0.50347900390626, 0.2066802978515626)] , and get:" +
                kdtree.range(r) + " and it took: " + time + " milliseconds."); */
    }
}




