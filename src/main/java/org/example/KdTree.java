package org.example;

import edu.princeton.cs.algs4.*;


import java.awt.*;
import java.util.ArrayList;
import java.util.function.DoubleToLongFunction;

public class KdTree {
    /*
    todo: Arrays of primitive types usually use 24 bytes of header information ( 16 bytes of a object overhead, 4 bytes
     for the length and 4 bytes for padding plus the memory needed to store the values. An array of objects uses 24 bytes
     of overhead plus 8N for each object; plus the object size
     todo: node memory: 8 bytes for each key and value, 8 * 4 = 32, 4 bytes for N, and 16 bytes of Object overhead,
     plus 2*8=16 bytes for the two left and right objects that are null by default, plus 8 bytes of extra overhead for a
     total of 76 bytes. The rest are as follows: boolean: 1, byte: 1, char: 2, int 4, float 4, long 8, double 8
    */
    private Node root;
    private Queue<Point2D> queue = new Queue<Point2D>();
    private Queue<Node> q = new Queue<>();
    private ArrayList<Point2D> points = new ArrayList<Point2D>();
    private MinPQ<Double> xCoordinates = new MinPQ<>();
    private boolean result = false;
    int redundantPoints = 0;
    private BST<Double, Double> intervalSearchTree = new BST();
    private int nodesVisited = 0;
    ArrayList<Point2D> pointsVisited = new ArrayList<>();

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
        Double minYInter = 0.0;
        Double maxYInter = 1.0;

        public Node(Point2D p, int n, boolean orientation, Node parent) {
            this.p = p;
            this.orientation = orientation;
            this.parent = parent;
            this.N = n;
            this.nodeRect = null;
        }

        @Override
        public int compareTo(Node h) {
            /*double thisX = this.p.x();
            double thisY = this.p.y();
            double hX = h.p.x();
            double hY = h.p.y();
            if (!this.orientation) {
                if (thisX < hX) {
                    return -1;
                }
                if (thisX > hX) {
                    return 1;
                }
            }
            if (this.orientation) {
                if (thisY < hY) {
                    return -1;
                }
                if (thisY > hY) {
                    return 1;
                }
            }
            return 0;
        }*/
            // cleaned up redundant caches
            if (!this.orientation) {
                if (this.xCoord < h.xCoord) {
                    return -1;
                }
                if (this.xCoord > h.xCoord) {
                    return 1;
                }
            }
            if (this.orientation) {
                if (this.yCoord < h.yCoord) {
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
        draw(root);
    }

    private void draw(Node h) {
        // convert the rectangle to
        RectHV tempRect;
        StringBuilder sb = new StringBuilder();
        if (!h.orientation) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.xCoord, h.yCoord);
            StdDraw.point(h.xCoord, h.yCoord);
            sb.append(h.xCoord + " ");
            sb.append(h.yCoord);
            StdDraw.text(h.xCoord, h.yCoord, sb.toString());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(h.xCoord, h.minYInter, h.xCoord, h.maxYInter);
            // if h is horizontal draw h's rectangle
            StdDraw.setPenRadius(0.005);
            // StdDraw.setPenColor(Color.MAGENTA);
            // drawRectangle(h);
            /*
            StdDraw.rectangle((h.maxXInter - h.minXInter) / 2, (h.maxYInter - h.minYInter) / 2,
                    (h.maxXInter - h.minXInter) / 2, (h.maxYInter - h.minYInter) / 2);
            if (h.left != null) {
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), h.xCoord, rectHV.ymax());
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(h.xCoord, rectHV.ymin(), rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
            }*/
        } else if (h.orientation) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.012);
            StdDraw.point(h.xCoord, h.yCoord);
            sb.append(h.xCoord);
            sb.append(h.yCoord);
            StdDraw.text(h.xCoord, h.yCoord, sb.toString());
            StdDraw.setPenRadius(0.003);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(h.minXInter, h.yCoord, h.maxXInter, h.yCoord);
            // or if h is vertical draw h's rectangle
            StdDraw.setPenRadius(0.005);
            // StdDraw.setPenColor(Color.MAGENTA);
            // drawRectangle(h);
            /*
            StdDraw.rectangle((h.maxXInter - h.minXInter) / 2, (h.maxYInter - h.minYInter) / 2,
                    (h.maxYInter - h.minYInter) / 2,(h.maxXInter - h.minXInter) / 2);
            if (h.left != null) {
                // the sub rectangles are different depending on parent axis orientation
                tempRect = new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), h.yCoord);
                draw(h.left, tempRect);
            }
            if (h.right != null) {
                tempRect = new RectHV(rectHV.xmin(), h.yCoord, rectHV.xmax(), rectHV.ymax());
                draw(h.right, tempRect);
            }*/
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
        return keys() == null;
    }

    private Iterable<Point2D> KDintersects(double lo, double hi) {
        double currentX;
        while (!xCoordinates.isEmpty()) {
            currentX = xCoordinates.delMin();
            // Get me all the points with this x coordinate and y between lo and hi
            Point2D start = new Point2D(currentX, lo);
            Point2D end = new Point2D(currentX, hi);
            for (Point2D p : keys(start, end)) {
                if (!points.contains(p)) points.add(p);
            }
        }
        return points;
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

    private int rank(Point2D p) {
        return rank(root, p);
    }

    private int rank(Node x, Point2D p) {
        if (x == null) return 0;
        int cmp = p.compareTo(x.p);
        if (cmp < 0) return rank(x.left, p);
        else if (cmp > 0) return 1 + size(x.left) + rank(x.right, p);
        else return size(x.left);
    }

    public boolean contains(Point2D p) {
        if (p.equals(null)) throw new IllegalArgumentException("You have to pass a valid point object");
        if (isEmpty()) return false;
        Node n = new Node(p, 1, false, root);
        n.xCoord = p.x();
        n.yCoord = p.y();
        if (root.xCoord == n.xCoord && root.yCoord == n.yCoord) return true;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return contains(root, n, p);
    }

    private boolean isInsideRectangle(Node h, double x, double y) {
        if (h.minXInter <= x && h.maxXInter > x && h.minYInter <= y && h.maxYInter > y) return true;
        return false;
    }

    private boolean contains(Node h, Node n, Point2D p) {
        if (h == null) result = false;
        if (h.xCoord == n.xCoord && h.yCoord == n.yCoord) result = true;
        int cmp = h.compareTo(n);
        n.orientation = !n.orientation; // is the point in the left rect or the right
        n.parent = h;

//        if (h.left != null && cmp > 0 && isInsideRectangle(h.left, n.xCoord, n.yCoord)) contains(h.left, n, p);
//        else if (h.right != null && cmp <= 0 && isInsideRectangle(h.right, n.xCoord, n.yCoord)) contains(h.right, n, p);
//        if (h.left != null && cmp > 0 && h.left.nodeRect.contains(p) && n.xCoord < h.left.maximumX) contains(h.left, n, p);
//        else if (h.right != null && cmp <= 0 && h.right.nodeRect.contains(p) && n.xCoord < h.right.maximumX) contains(h.right, n, p);
//        if (h.left != null && cmp > 0 && n.xCoord < h.left.maximumX) contains(h.left, n, p);
//        else if (h.right != null && cmp <= 0 && n.xCoord < h.right.maximumX) contains(h.right, n, p);
        if (h.left != null) {
            buildChildRectangle(h, h.left);
            if (cmp > 0 && h.left.nodeRect.contains(p)) contains(h.left, n, p);
        }
        if (h.right != null) {
            buildChildRectangle(h, h.right);
            if (cmp <= 0 && h.right.nodeRect.contains(p)) contains(h.right, n, p);
        }
        return result;
    }

    private boolean nodeRectContains(Node h, double x, double y) {
        if (h.minXInter < x && h.maxXInter > x && h.minYInter < y && h.maxYInter > y) return true;
        else return false;
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
        points = new ArrayList<>();
        if (rect == null) throw new IllegalArgumentException("rectangle has to be a valid " +
                "object. ");
        // does the rectangle contain the root?
        return range(root, rect);
    }

    private Iterable<Point2D> range(Node h, RectHV rect) {
        /* check the subtrees. remember you have to check both sides if rect intersects the line through the point.
         * Is the horizontal node's line between rectangle's minx and maxx or a vertical node's line between the
         * rectangle's miny and maxy */
        if (h == null) return points;
        if (rect.contains(h.p)) points.add(h.p);
        if ((!h.orientation && (rect.xmin() < h.xCoord && rect.xmax() > h.xCoord)) ||
                ((h.orientation) && (rect.ymin() < h.yCoord && rect.ymax() > h.yCoord))) {
            // check both sides of the tree
            range(h.left, rect);
            range(h.right, rect);
        } else if (((!h.orientation) && (rect.xmax() < h.xCoord)) || ((h.orientation) && (rect.ymax() < h.yCoord))) {
            // It is only on the left/bottom side so only check the left/bottom side of the tree
            range(h.left, rect);
        } else if (((!h.orientation) && (rect.xmin() > h.xCoord)) || (((h.orientation) && (rect.ymin() > h.yCoord)))) {
            // It is only on the right/top side so only check the right/top
            range(h.right, rect);
        }
        return points;
    }

    private void buildChildRectangle(Node parent, Node child) {
        if (!parent.orientation) {
            RectHV left = new RectHV(parent.minXInter, parent.minYInter, child.xCoord, parent.maxYInter);
            if (parent.left != null) parent.left.nodeRect = left;
            RectHV right = new RectHV(child.xCoord, parent.minYInter, parent.maxXInter, parent.maxYInter);
            if (parent.right != null) parent.right.nodeRect = right;
        } else if (parent.orientation) {
            RectHV left = new RectHV(parent.minXInter, parent.minYInter, parent.maxXInter, child.yCoord);
            if (parent.left != null) parent.left.nodeRect = left;
            RectHV right = new RectHV(parent.minXInter, child.yCoord, parent.maxXInter, parent.maxYInter);
            if (parent.right != null) parent.right.nodeRect = right;
        }

    }

    private void setLeftRectIntervals(Node x) {

        RectHV left;
        if (!x.orientation) { // Horizontal node
            // left = new RectHV(x.parent.minXInter, x.parent.minYInter, x.parent.p.x(), x.parent.maxYInter);
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.yCoord;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        } else {
            // vertical node
            // left = new RectHV(x.parent.minXInter, x.parent.minYInter, x.parent.maxXInter, x.parent.p.y());
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.xCoord;
            x.maxYInter = x.parent.maxYInter;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        }
    }

    private void setRightRectIntervals(Node x) {
        RectHV right;

        if (!x.orientation) { //
            // horizontal node
            // right = new RectHV(x.parent.p.x(), x.parent.minYInter, x.parent.maxXInter, x.parent.maxYInter);
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.yCoord;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.maxYInter;
            //intervalSearchTree.put(x.minYInter, x.maxYInter, x.xCoord);
        }
        if (x.orientation) {
            // vertical node
            // right = new RectHV(x.parent.minXInter, x.parent.p.y(), x.parent.maxXInter, x.parent.maxYInter);
            x.minXInter = x.parent.xCoord;
            x.minYInter = x.parent.minYInter;
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
            // intervalSearchTree.put(0.0, 1.0, 1.0);
            return newNode;
        } else {
            newNode.orientation = !h.orientation;
            int cmp = h.compareTo(newNode);
            newNode.parent = h;
            if (cmp < 0) {  // It means root is smaller than the new node
                // xCoordinates.insert(newNode.p.x());
                h.right = insert(h.right, newNode);
                setRightRectIntervals(h.right);
                h.maximumX = Math.max(h.maximumX, h.right.maximumX);
                h.maximumY = Math.max(h.maximumY, h.right.maximumY);
            } else if (cmp > 0) {  // it means root is larger than the new node
                // xCoordinates.insert(newNode.p.x());
                h.left = insert(h.left, newNode);
                setLeftRectIntervals(h.left);
                h.maximumX = Math.max(h.maximumX, h.left.maximumX);
                h.maximumY = Math.max(h.maximumY, h.left.maximumY);
            } else {
                redundantPoints++;
            }
        }
        assert h.minYInter <= h.maxYInter : "minimum y is more than maximum y";
        assert h.minXInter <= h.maxXInter : "minimum x is more than maximum x";
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
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        return x.N;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Data passed to nearest() can not be null.");
        if (root == null) throw new IllegalArgumentException("The tree is empty.");
        if (contains(p)) return p;
        Point2D nearestNeig = root.p;
        pointsVisited.add(nearestNeig);
        nodesVisited++;
        System.out.println("Tree size : " + root.N);
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);
        return nearest(root, p, nearestNeig);
    }

    private Point2D nearest(Node h, Point2D p, Point2D nearstP) {
        RectHV rHl = null;
        RectHV rHr = null;
        h.xCoord = h.p.x();
        h.yCoord = h.p.y();
        if (h == null) return nearstP;
        if (h.parent == null) {
            rHl = new RectHV(0.0, 0.0, h.xCoord, 1.0);
            rHr = new RectHV(h.xCoord, 0.0, 1.0, 1.0);
        } else if (h.parent != null) {
            if (!h.orientation) {
                rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.xCoord, h.nodeRect.ymax());
                rHr = new RectHV(h.xCoord, h.nodeRect.ymin(), h.nodeRect.xmax(), h.nodeRect.ymax());
            } else if (h.orientation) {
                rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.yCoord);
                rHr = new RectHV(h.nodeRect.xmin(), h.yCoord, h.nodeRect.xmax(), h.nodeRect.ymax());
            }
        }
        if (!rHl.contains(p) || !(rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            if (h.left != null) {
                h.left.nodeRect = rHl;
                h.left.parent = h;
            }

            if (h.right != null) {
                h.right.nodeRect = rHr;
                h = h.right;
                nearstP = nearest(h, p, nearstP);
            }
        } else if (!rHr.contains(p) && !(rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            h.right.nodeRect = rHr;
            h.right.parent = h;
            if (h.left != null) {
                h.left.nodeRect = rHl;
                h = h.left;
                nearstP = nearest(h, p, nearstP);
            }
        }
        else if (rHl.contains(p) && (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            // check rHl for points
            if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                if (h.left != null) {
                    nodesVisited++;
                    System.out.println("Tree size : " + h.N);
                    if (h.left.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                        nearstP = h.left.p;
                        pointsVisited.add(nearstP);
                    }
                    h.left.parent = h;
                    if (h.right != null) {
                        h.right.parent = h;
                        h.right.nodeRect = rHr;
                    }
                    h.left.nodeRect = rHl;
                    nearstP = nearest(h.left, p, nearstP);
                    // nearstP = nearest(h.right, p, nearstP);
                }
            }
        /*if (h.left != null) {
            if (rHl.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP)) {
                nodesVisited++;
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
                nodesVisited++;
                if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                    nearstP = h.right.p;
                }
            }
            h.right.parent = h;
            h.right.nodeRect = rHr;
            nearstP = nearest(h.right, p, nearstP);
        }
         if (h.orientation) {
            rHl = new RectHV(h.nodeRect.xmin(), h.nodeRect.ymin(), h.nodeRect.xmax(), h.yCoord);
            rHr = new RectHV(h.nodeRect.xmin(), h.yCoord, h.nodeRect.xmax(), h.nodeRect.ymax());
            // rHr = new RectHV(h.xCoord,h.nodeRect.ymin(),h.nodeRect.xmax(),h.nodeRect.ymax());
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
        } */
        } else if (rHr.contains(p) && (rHr.distanceSquaredTo(p) < p.distanceSquaredTo(nearstP))) {
            // check rHr

            if (h.right != null) {
                nodesVisited++;
                System.out.println("Tree size : " + h.N);
                if (h.right.p.distanceSquaredTo(p) < nearstP.distanceSquaredTo(p)) {
                    nearstP = h.right.p;
                    pointsVisited.add(nearstP);
                }
                h.right.parent = h;
                h.left.parent = h;
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

    private void listParents(Node h) {
        if (h.parent == null) return;
        System.out.println(h.parent.xCoord + "" + h.parent.yCoord);
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
        String filename = args[0];
        In in = new In(filename);
        int pointsCounter = 0;
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            pointsCounter++;
            kdtree.insert(p);
        }
        System.out.println("Tree size : " + kdtree.size());
        System.out.println("The number of points : " + pointsCounter);
        // kdtree.draw();
        // RectHV r = new RectHV(0.2, 0.14, 0.4, 0.18);
        // System.out.println("Here are the points in the rectangle" + kdtree.range(r));
        // System.out.println("Here is the size of the tree. " + kdtree.size());
        System.out.println("Here is the nearest node to 0.81, 0.30: " + kdtree.nearest(new Point2D(0.81, 0.30)));
        System.out.println("The number of nodes visited is:  " + kdtree.nodesVisited);
        System.out.println("Here are the points visited to get to the nearest neighbor. ");
        for (Point2D p : kdtree.pointsVisited) {
            System.out.println(p);
        }
    }
}

