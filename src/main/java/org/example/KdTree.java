package org.example;

import edu.princeton.cs.algs4.*;

public class KdTree {
    private Node root;
    private Queue q = new Queue();

    private class Node implements Comparable<Node> {
        Point2D p; // key
        Node left, right; // subtrees
        int N; // # nodes in this subtree
        boolean coordinate;// 0 means horizontal

        public Node(Point2D p, int N, boolean coordinate) {
            this.p = p;

            this.N = N;
            this.coordinate = coordinate;
        }

        /* Check to make sure this method checks the coordinate of what is already on the org.example.KDTree with the new node
         * not the other way around */
        @Override
        public int compareTo(Node o) {
            if (o.coordinate == false) {
                if (this.p.x() < o.p.x()) return -1;
                else return 1;
            }
            if (o.coordinate == true) {
                if (this.p.y() < o.p.y()) return -1;
                else return 1;
            }
            return 0;
        }
    }

    public void draw() {
        ///todo - Implement; it is not done yet
        StdDraw.clear();
        StdDraw.setPenRadius(0.005);
        for (Node n : this.keys()) {
            StdDraw.point(n.p.x(), n.p.y());
            StdDraw.setPenColor(StdDraw.RED);
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

    private void makeVertical(Node x) {
        if (x == null) return;
        x.coordinate = true;
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
            return new Node(p, 0, false);
        }
        int cmp = p.compareTo(h.p);
        if (cmp < 0) h.left = insert(h.left, p);
        else if (cmp > 0) h.right = insert(h.right, p);
        else h.p = p;
        /* Test to make sure the line below is all you need to flip coordinates */
        if (isHorizontal(h)) makeVertical(h.right);
        if (isHorizontal(h)) makeVertical(h.left);
        h.N = size(h.left) + size(h.right);
        return h;
    }

    public Point2D nearest(Point2D p) {
        ///todo - Implement
        Point2D point = new Point2D(0,0);
        return p;
    }

    public static void main(String[] args) {
        KdTree k = new KdTree();
        for (int i = 0; i < 20; i++) {
            Point2D p = new Point2D(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0));
            k.insert(p);
            //StdOut.println(p);
        }
        StdOut.println("Finished w/o errors.");
        int index = 1;
        for (Node n : k.keys()) {
            if (n.coordinate == true) {
                StdOut.println(index + "-" + n.p);
                index++;
            }
        }

    }
}
