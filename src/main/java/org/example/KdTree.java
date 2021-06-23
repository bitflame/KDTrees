package org.example;

import edu.princeton.cs.algs4.*;

public class KdTree {
    private Node root;
    private Queue q = new Queue();

    private static class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right, parent; // subtrees
        int N; // # nodes in this subtree
        boolean coordinate;// 0 means horizontal
        private RectHV rect; // the axis-aligned rectangle corresponding to this node

        public Node(Point2D p, int N, boolean coordinate, Node parent) {
            this.p = p;
            this.coordinate = coordinate;
            this.parent = parent;
            this.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        }

        /* Check to make sure this method checks the coordinate of what is already on the org.example.KDTree with the new node
         * not the other way around */
        @Override
        public int compareTo(Node o) {
//            if (o.coordinate == false) {
//                if (this.p.x() < o.p.x()) return -1;
//                else return 1;
//            }
//            if (o.coordinate == true) {
//                if (this.p.y() < o.p.y()) return -1;
//                else return 1;
//            }
            if (isHorizontal(this)) {
                if (this.p.x() < o.p.x()) return -1;
                else return 1;
            } else if (isVertical(this)) {
                if (this.p.y() < o.p.y()) return -1;
                else return 1;
            } else return 0;
        }
    }

    public void draw() {
        /*StdDraw.clear();
        StdDraw.setPenRadius(0.008);
        for (Node n : this.keys()) {
            if (n.parent == null) {  // Horizontal Devide
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), 0, n.p.x(), 1.0);
                *//* If n is vertical and it is smaller than its parent *//*
            }
            if (isVertical(n.parent)) {  // Vertical devide
                if (n.parent.compareTo(n) > 0) {
                    StdDraw.setPenRadius(0.008);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.point(n.p.x(), n.p.y());
                    StdDraw.setPenRadius(0.003);
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.line(n.p.x(), 0, n.p.x(), n.parent.p.y());
                } else if (n.parent.compareTo(n) < 0) {
                    StdDraw.setPenRadius(0.008);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.point(n.p.x(), n.p.y());
                    StdDraw.setPenRadius(0.003);
                    StdDraw.setPenColor(StdDraw.RED);
                    StdDraw.line(n.p.x(), n.parent.p.y(), n.p.x(), 1.0);
                }
            } else if (isHorizontal(n.parent)) {
                if (n.parent.compareTo(n) > 0) {
                    StdDraw.setPenRadius(0.008);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.point(n.p.x(), n.p.y());
                    StdDraw.setPenRadius(0.003);
                    StdDraw.setPenColor(StdDraw.BLUE);
                    StdDraw.line(0, n.p.y(), n.parent.p.x(), n.p.y());
                } else if (n.parent.compareTo(n) < 0) {
                    StdDraw.setPenRadius(0.008);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.point(n.p.x(), n.p.y());
                    StdDraw.setPenRadius(0.003);
                    StdDraw.setPenColor(StdDraw.BLUE);
                    StdDraw.line(n.parent.p.x(), n.p.y(), 1.0, n.p.y());
                }
            }
        }*/
        /* Drawing the rectangles now. The old code is in the commented section above. */

    }

    public Point2D get(Point2D p) {
        return get(root, p);
    }

    private Point2D get(Node h, Point2D p) {
        if (h == null) return null;
        int cmp = p.compareTo(h.p);
        if (cmp < 0) return get(h.left, h.p);
        else if (cmp > 0) return get(h.right, h.p);
        else return h.p;
    }

    public Iterable<Node> keys() {
        return keys(root);
    }

    private Queue<Node> keys(Node h) {

        if (h != null) q.enqueue(h);
        if (h.left != null) {
            keys(h.left);
        }
        if (h.right != null) {
            keys(h.right);
        }
        return q;
    }

    ///todo - implement the node's rectangle
    public Iterable<Point2D> range(RectHV rect) {
        range(root, rect);
        return q;
    }

    private Iterable<Point2D> range(Node h, RectHV rect) {
        if (h.rect.intersects(rect)) {
            if (rect.contains(h.p)) q.enqueue(h.p);
            /* only look at the left and right children's rectangles if the root's rectangle intersects with the desired
             * rectangle area */
            range(h.left, rect);
            range(h.right, rect);
        }
        return q;
    }

    private static boolean isHorizontal(Node x) {
        if (x == null) return false;
        return x.coordinate == false;
    }

    private static boolean isVertical(Node x) {
        if (x == null) return false;
        return x.coordinate == true;
    }

    private void makeVertical(Node x) {
        if (x == null) return;
        x.coordinate = true;
    }

    private void makeHorizontal(Node x) {
        if (x == null) return;
        x.coordinate = false;
    }


    public void insert(Point2D p) {
        root = insert(root, p);
    }

    private void addRect(Node n, Node parent) {
        n.rect = new RectHV(0.0, 0.0, parent.p.x(), 1.0);
    }

    private Node insert(Node h, Point2D p) {
        if (h == null) {
            return new Node(p, 1, false, null);
        }
        if (isHorizontal(h) && p.x() < h.p.x()) {
            h.left = insert(h.left, p);
            h.left.rect = new RectHV(h.rect.xmin(), h.rect.ymin(), h.p.x(), h.rect.ymax());
            h.left.parent = h;
            makeVertical(h.left);
        } else if (isHorizontal(h) && p.x() > h.p.x()) {
            h.right = insert(h.right, p);
            h.right.rect = new RectHV(h.p.x(), h.rect.ymin(), h.rect.xmax(), h.rect.ymax());
            h.right.parent = h;
            makeVertical(h.right);
        } else if (isVertical(h) && p.y() < h.p.y()) {
            h.left = insert(h.left, p);
            h.left.rect = new RectHV(h.rect.xmin(), h.rect.ymin(), h.rect.xmax(), h.p.y());
            h.left.parent = h;
            makeHorizontal(h.left);
        } else if (isVertical(h) && p.y() > h.p.y()) {
            h.right = insert(h.right, p);
            h.right.rect = new RectHV(h.rect.xmin(), h.p.y(),h.rect.xmax(),h.rect.ymax());
            h.right.parent = h;
            makeHorizontal(h.right);
        }

//        int cmp = p.compareTo(h.p);
//        if (cmp < 0) {
//            h.left = insert(h.left, p);
//            h.left.parent = h;
//        } else if (cmp > 0) {
//            h.right = insert(h.right, p);
//            h.right.parent = h;
//        } else h.p = p;
//        if (isVertical(h)) {
//            makeHorizontal(h.right);
//            makeHorizontal(h.left);
//        } else if (isHorizontal(h)) {
//            makeVertical(h.left);
//            makeVertical(h.right);
//        }
        return h;
    }

    public Point2D nearest(Point2D p) {
        ///todo - Implement
        /*if the closest point discovered so far is closer than the distance between the query point and the rectangle
        corresponding to a node, there is no need to explore that node (or its subtrees).*/
        Point2D nearestNeig = root.p;
        if (root.left.rect.distanceTo(p) < p.distanceTo(nearestNeig)) nearest(root.left, p, nearestNeig);
        if (root.right.rect.distanceTo(p) < p.distanceTo(nearestNeig)) nearest(root.right, p, nearestNeig);
        return p;
    }

    private Point2D nearest(Node n, Point2D p, Point2D nearstP) {
        if (n.p.distanceTo(p) < p.distanceTo(nearstP)) nearstP = n.p;
        if (n.left.rect.distanceTo(p) < p.distanceTo(nearstP)) nearest(n.left, p, nearstP);
        if (n.right.rect.distanceTo(p) < p.distanceTo(nearstP)) nearest(n.right, p, nearstP);
        return nearstP;
    }

    public static void main(String[] args) {
        StdOut.println("Changed something for testing.");
        KdTree k = new KdTree();
        Queue<Point2D> s = new Queue<>();
        Point2D p1 = new Point2D(0.7, 0.2);
        s.enqueue(p1);
        Point2D p2 = new Point2D(0.5, 0.4);
        s.enqueue(p2);
        Point2D p3 = new Point2D(0.2, 0.3);
        s.enqueue(p3);
        Point2D p4 = new Point2D(0.4, 0.7);
        s.enqueue(p4);
        Point2D p5 = new Point2D(0.9, 0.6);
        s.enqueue(p5);
        for (Point2D p : s) {
            k.insert(p);
        }
        k.draw();
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
