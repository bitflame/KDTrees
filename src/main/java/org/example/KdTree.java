package org.example;

import edu.princeton.cs.algs4.*;


import java.util.ArrayList;


public class KdTree {
    private class IntervalST<Key extends Comparable<Key>, Value> {
        private void put(Key lo, Key hi, Value val) {

        }

        private Value get(Key lo, Key hi) {

        }

        void delete(Key lo, Key hi) {

        }

        Iterable<Value> intersects(Key lo, Key hi) {

        }

        private class Node {
            Key lo;
            Key hi;
            Value val;

            public void Node(Key lo, Key hi, Value val) {
                this.lo = lo;
                this.hi = hi;
                this.val = val;
            }
        }
    }

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
    // private int level = 0;
    private int nodesVisited = 0;

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
            /*if (!this.orientation) {
                if (this.xCoord == h.xCoord && this.yCoord == h.yCoord) return 0;
                if (this.xCoord <= h.xCoord) {
                    return -1;
                }
                if (this.xCoord > h.xCoord) {
                    return 1;
                }
            }
            if (this.orientation) {
                if (this.yCoord <= h.yCoord) {
                    return -1;
                }
                if (this.yCoord > h.yCoord) {
                    return 1;
                }
            }
            return 0;*/
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
        draw(root);
    }

    private void draw(Node h) {
        // convert the rectangle to
        RectHV tempRect;
        StringBuilder sb = new StringBuilder();
        if (h.level % 2 == 0) {
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
        } else if (h.level % 2 != 0) {
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
        return size() == 0;
    }

    /*private Iterable<Point2D> KDintersects(double lo, double hi) {
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
    }*/

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

    private int rank(Point2D pnt) {
        return rank(root, pnt);
    }

    private int rank(Node x, Point2D point) {
        if (x == null) return 0;
        int cmp = x.p.compareTo(point);
        if (cmp < 0) return 1 + size(x.left) + rank(x.right, point);
        else if (cmp >= 0) return rank(x.left, point);
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
        // get the minimum
        // get its ceiling until you see all the points and make sure that you do see all the points

        /*Node n = new Node(point, 1, null);
        n.xCoord = point.x();
        n.yCoord = point.y();
        if (root.p.equals(point)) return result = true;
        else return contains(root, n);*/
        // return result;
    }

    private boolean pointIsInsideRectangle(double px, double py, double minx, double miny, double maxx, double maxy) {
        if (minx <= +px && maxx >= px && miny <= py && maxy >= py) return true;
        return false;
    }

    private Point2D ceiling(Point2D point) {
        Node x = ceiling(root, point);
        if (x == null) return null;
        return x.p;
    }

    private Node ceiling(Node x, Point2D point) {
        if (x == null) return null;
        Node n = new Node(point, 1, null);
        int cmp = x.compareTo(n);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, point);
        Node t = ceiling(x.left, point);
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
        else if (isEmpty()) return null;
        root.nodeRect = new RectHV(0.0, 0.0, 1.0, 1.0);


        return range(root, rect);
    }

    private Iterable<Point2D> range(Node h, RectHV rectHV) {
        /*start from minimum x to maximum and add the ymin-ymax interval to a BST. When you get to the rectHV's minx, do
         * a range search in BST for all the intersecting y coordinates until you get to the rectHV's maxx. Do not forget
         * to remove the rectangles once sweep-line passes the rectangle. So add when you get to the minx, and remove when
         * you get to maxx of the data. Do a range search when you get to the minx of rectHV and continue until you get to
         * the rectHV's maxx */
        /* check the subtrees. remember you have to check both sides if rect intersects the line through the point.
         * Is the horizontal node's line between rectangle's minx and maxx or a vertical node's line between the
         * rectangle's miny and maxy */
        if (h == null) {
            return points;
        }
        double currentX;
        h.minXInter = h.nodeRect.xmin();
        h.minYInter = h.nodeRect.ymin();
        h.maxXInter = h.nodeRect.xmax();
        h.maxYInter = h.nodeRect.ymax();
        // if (rect.contains(h.p) && (!points.contains(h.p))) points.add(h.p);
        while (!xCoordinates.isEmpty()) {
            currentX = xCoordinates.delMin();

            //if (currentX == h.minXInter) intSearchTree.put(h.minYInter, h.maxYInter);
            //if (currentX == h.maxXInter) intSearchTree.delete(h.minYInter);
            if (currentX >= rectHV.xmin() && currentX <= rectHV.xmax()) {
                /* rectHV give the xmin and xmax that I need to check, and the intSearchTree gives different
                 * minY and maxYs for the areas. There are of course likely more than one intersecting rectangles */
                //for (Double d : intSearchTree.keys()) {
                    /* Now I have miny, and maxy for this coordinate. I may need to change rank to take Point2D, then
                    use it to get the rank of high and low, and then
                    * use select() or get() to get any points within these boundaries */
                //Point2D low = new Point2D(currentX, d);
                //select(rank(low));// this should give the lower point in my tree
                //Point2D high = new Point2D(currentX, intSearchTree.get(d));
                //select(rank(high));// this is the higher point in my tree
                //for (Point2D p : keys(select(rank(low)).p, select(rank(high)).p)) {
                //  if (!points.contains(p)) points.add(p);
                //}
                //}
            }
        }

        /*else{
            Point2D temp = h.p;
            if (rectHV.contains(temp) && (!points.contains(temp))) {
                points.add(temp);
            }

            buildChildRectangle(h, h.left, h.right);
            if (rectHV.intersects(h.nodeRect)) {
                range(h.left, rectHV);
                range(h.right, rectHV);
            } else {
                if ((h.left == null) || (!rectHV.intersects(h.left.nodeRect))) {
                    range(h.right, rectHV);
                }
                if ((h.right == null) || (!rectHV.intersects(h.right.nodeRect))) {
                    range(h.left, rectHV);
                }
                if ((h.left != null) && rectHV.intersects(h.left.nodeRect)) range(h.left, rectHV);
                if ((h.right != null) && rectHV.intersects(h.right.nodeRect)) range(h.right, rectHV);
            }
        }*/

        //if ((!h.orientation && (rect.xmin() < h.xCoord && rect.xmax() > h.xCoord)) ||
        //((h.orientation) && (rect.ymin() < h.yCoord && rect.ymax() > h.yCoord))) {
        // check both sides of the tree
        // range(h.left, rect);
        // range(h.right, rect);
        // } else
        //if (((h.level % 2 == 0) && (rectXmax < h.xCoord)) || ((h.level % 2 != 0) && (rectYmax < h.yCoord))) {
        // It is only on the left/bottom side so only check the left/bottom side of the tree
        // range(h.left, rect);
        // if (h.left != null) h = h.left;
        //if (h.left != null) range(h.left, rectXmin, rectYmin, rectXmax, rectYmax);
        // range(h,rect);
        // } else if (((h.level % 2 == 0) && (rectXmin >= h.xCoord)) || (((h.level % 2 != 0) && (rectYmin >= h.yCoord)))) {
        // It is only on the right/top side so only check the right/top
        // range(h.right, rect);
        //if (h.right != null) range(h.right, rectXmin, rectYmin, rectXmax, rectYmax);
        // range(h,rect);
        //}
        //if (h.xCoord >= rectXmin && h.xCoord <= rectXmax && h.yCoord >= rectYmin && h.yCoord <= rectYmax && (!points.contains(h.p))) points.add(h.p);
        //range(h.left, rectXmin, rectYmin, rectXmax, rectYmax);
        //range(h.right, rectXmin, rectYmin, rectXmax, rectYmax);
        return points;
    }

    private void buildChildRectangle(Node parent, Node rightChild, Node leftChild) {
        if (parent.level % 2 != 0) {
            // RectHV left = new RectHV(parent.minXInter, parent.minYInter, parent.xCoord, parent.maxYInter);

            // if (parent.left != null) parent.left.nodeRect = left;
            if (leftChild != null) {
                leftChild.nodeRect = new RectHV(parent.minXInter, parent.minYInter, parent.xCoord, parent.maxYInter);
//                parent.left.minXInter = parent.minXInter;
//                parent.left.minYInter = parent.minYInter;
//                parent.left.maxXInter = parent.xCoord;
//                parent.left.maxYInter = parent.maxYInter;
            }

            // if (parent.right != null) parent.right.nodeRect = right;
            if (rightChild != null) {
                rightChild.nodeRect = new RectHV(parent.xCoord, parent.minYInter, parent.maxXInter, parent.maxYInter);
//                parent.right.minXInter = parent.right.xCoord;
//                parent.right.minYInter = parent.minYInter;
//                parent.right.maxXInter = parent.maxXInter;
//                parent.right.maxYInter = parent.maxYInter;
            }
        } else if (parent.level % 2 == 0) {

            // if (parent.left != null) parent.left.nodeRect = left;
            if (leftChild != null) {
                leftChild.nodeRect = new RectHV(parent.minXInter, parent.minYInter, parent.maxXInter, parent.yCoord);
//                parent.left.minXInter = parent.minXInter;
//                parent.left.minYInter = parent.minYInter;
//                parent.left.maxXInter = parent.maxXInter;
//                parent.left.maxYInter = parent.left.yCoord;
            }

            // if (parent.right != null) parent.right.nodeRect = right;
            if (rightChild != null) {
                rightChild.nodeRect = new RectHV(parent.minXInter, parent.yCoord, parent.maxXInter, parent.maxYInter);
//                parent.right.minXInter = parent.minXInter;
//                parent.right.minYInter = parent.right.yCoord;
//                parent.right.maxXInter = parent.maxXInter;
//                parent.right.maxYInter = parent.maxYInter;
            }
        }

    }

    /* private void setLeftRectIntervals(Node x) {
        RectHV left;
        if (!x.orientation) {
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.yCoord;
        } else {
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.xCoord;
            x.maxYInter = x.parent.maxYInter;
        }
    }

    private void setRightRectIntervals(Node x) {
        RectHV right;
        if (!x.orientation) {
            x.minXInter = x.parent.minXInter;
            x.minYInter = x.parent.yCoord;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.maxYInter;
        }
        if (x.orientation) {
            x.minXInter = x.parent.xCoord;
            x.minYInter = x.parent.minYInter;
            x.maxXInter = x.parent.maxXInter;
            x.maxYInter = x.parent.maxYInter;
        }
    } */

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("You can not insert null object" +
                "into the tree");
        root = insert(root, p);
        root.maximX = Math.max(root.xCoord, p.x());
    }

    /* Do not create the new node and assign parent, and orientation until you have a null link to place it on. Just
     * compare the point with the current node's point and traverse the tree to find the place the point goes into. That
     * means pas the point to the private insert() method instead of newNode */
    private Node insert(Node h, Point2D p) {
        if (h == null) {
            Node n = new Node(p, 1, null);
            n.xCoord = p.x();
            xCoordinates.insert(n.xCoord);
            n.yCoord = p.y();
            h = n;
            return h;
        }
        if (h.level % 2 == 0) {
            if (h.xCoord == p.x() && h.yCoord == p.y()) h.p = p;
            else if (h.xCoord <= p.x()) {
                h.right = insert(h.right, p);
                h.right.level = h.level + 1;
                h.maximX = Math.max(h.maximX, h.right.maximX);
            } else {
                h.left = insert(h.left, p);
                h.left.level = h.level + 1;
                // h.maximX = Math.max(h.maximX, h.left.maximX);
            }
        } else {
            // h is vertical
            if (h.xCoord == p.x() && h.yCoord == p.y()) h.p = p;
            else if (h.yCoord <= p.y()) {
                h.right = insert(h.right, p);
                h.right.level = h.level + 1;
                // h.maximX = Math.max(h.maximX, h.right.maximX);
            } else {
                h.left = insert(h.left, p);
                h.left.level = h.level + 1;
                // h.maximX = Math.max(h.maximX, h.left.maximX);
            }
        }
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


    public static void main(String[] args) {
        /* Test all the files to see if they load ok, and seem to produce the right rectangles and etc. */
        //Point2D p = new Point2D(0.5, 0.5);
        KdTree kdtree = new KdTree();
        /* for (int i = 0; i < 20; i++) {
            kdtree.insert(p);
        }
        System.out.println(kdtree.size()); */
        String filename = args[0];
        In in = new In(filename);
        // System.out.println("isEmpty() should be true. " + kdtree.isEmpty());
        // Stopwatch timer = new Stopwatch();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            kdtree.size();
            kdtree.isEmpty();
        }
        RectHV r = new RectHV(0.50347900390625, 0.2066802978515625, 0.5950927734375, 0.2689208984375);
        System.out.println(" rectangle: " + r + " contains the following points: " + kdtree.range(r));
        // System.out.println("put 1000000 nodes in the tree. ");
        // double time = timer.elapsedTime();
        // System.out.println("It took " + time + "to insert and run size() and isEmpty() for 1M nodes. ");
        // System.out.println("Tree size : " + kdtree.size());
        // System.out.println("isEmpty should be false " + kdtree.isEmpty());
        // kdtree.draw();
        // RectHV r = new RectHV(0.2, 0.14, 0.8, 0.95);
        // RectHV r = new RectHV(0.675, 0.1875, 0.9375, 0.5);
        //RectHV r = new RectHV(0.25, 0.0, 0.625, 0.75);
        //RectHV r = new RectHV(0.39, 0.03, 0.72, 0.88);
        // RectHV r = new RectHV(0.175, 0.281, 0.742, 0.97);distinct points rectangle
        //RectHV r = new RectHV(0.479, 0.198, 0.894, 0.676);
        //RectHV r = new RectHV(0.125, 0.25, 0.5, 0.625);
        // RectHV r = new RectHV(0.0537109375, 0.3154296875, 0.8876953125, 0.7919921875);

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

