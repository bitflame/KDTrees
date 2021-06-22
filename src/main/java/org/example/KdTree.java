package org.example;

import edu.princeton.cs.algs4.*;

public class KdTree {
    private Node root;
    private Queue q = new Queue();

    private class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right, parent; // subtrees
        int N; // # nodes in this subtree
        boolean coordinate;// 0 means horizontal

        public Node(Point2D p, int N, boolean coordinate, Node parent) {
            this.p = p;
            this.N = N;
            this.coordinate = coordinate;
            this.parent = parent;
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
            if (this.p.x() < o.p.x()) return -1;
            else return 1;
            // return 0;
        }
    }

    public void draw() {
        StdDraw.clear();
        StdDraw.setPenRadius(0.008);
        for (Node n : this.keys()) {
            if (n.parent == null) {
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), 0, n.p.x(), 1.0);
                /* If n is vertical and it is smaller than its parent */
            } else if (n.coordinate && n.compareTo(n.parent) < 0) {
                StdDraw.setPenRadius(0.008);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(0, n.p.y(), n.parent.p.x(), n.p.y());
            }
            /* If n is vertical and it is larger than its parent */
            else if (n.coordinate && n.compareTo(n.parent) > 0) {
                StdDraw.setPenRadius(0.008);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.BLUE);
                //StdDraw.line(n.parent.p.x(), n.p.y(), 1.0, n.p.y());
                StdDraw.line(n.parent.p.x(), n.p.y(), 1.0, n.p.y());
            }
            /* If n is horizontal and it is smaller than its parent */
            else if (!n.coordinate && n.compareTo(n.parent) < 0) {
                StdDraw.setPenRadius(0.008);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), 0, n.p.x(), n.parent.p.y());
            }
            /* If n is horizontal and it is larger than its parent */
            else {
                StdDraw.setPenRadius(0.006);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.point(n.p.x(), n.p.y());
                StdDraw.setPenRadius(0.003);
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(n.p.x(), n.parent.p.y(), n.p.x(), 1.0);
            }
        }
    }

    private Point2D get(Point2D p) {
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

    public Iterable<Point2D> range(RectHV rect) {
        return q;
    }

    private boolean isHorizontal(Node x) {
        if (x == null) return false;
        return x.coordinate == false;
    }

    private boolean isVertical(Node x) {
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

    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.N;
    }

    public void insert(Point2D p) {
        root = insert(root, p);
    }

    private Node insert(Node h, Point2D p) {
        if (h == null) {
            return new Node(p, 1, false, null);
        }
        if (p.x() < h.p.x() || p.y() < h.p.y()) {
            h.left = insert(h.left, p);
            h.left.parent = h;
        } else {
            h.right = insert(h.right, p);
            h.right.parent = h;
        }
//        int cmp = p.compareTo(h.p);
//        if (cmp < 0) {
//            h.left = insert(h.left, p);
//            h.left.parent = h;
//        } else if (cmp > 0) {
//            h.right = insert(h.right, p);
//            h.right.parent = h;
//        } else h.p = p;
        /* Test to make sure the line below is all you need to flip coordinates */
        if (isVertical(h)) {
            makeHorizontal(h.right);
            makeHorizontal(h.left);
        } else if (isHorizontal(h)) {
            makeVertical(h.left);
            makeVertical(h.right);
        }
        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    public Point2D nearest(Point2D p) {
        ///todo - Implement
        Point2D point = new Point2D(0, 0);
        return p;
    }

    public static void main(String[] args) {
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
