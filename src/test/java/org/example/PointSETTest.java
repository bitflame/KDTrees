package org.example;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointSETTest {
    @BeforeAll
    void init() {
        PointSET ps = new PointSET();
        In in = new In();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            ps.insert(p);
        }
        ps.draw();
    }

    @Test
    void isEmpty() {
    }

    @Test
    void insert() {
    }

    @Test
    void contains() {
    }

    @Test
    void size() {
    }

    @Test
    void nearest() {
    }

    @Test
    void draw() {
    }

    @Test
    void range() {
    }
}