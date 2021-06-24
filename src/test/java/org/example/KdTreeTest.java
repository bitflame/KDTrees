package org.example;

import edu.princeton.cs.algs4.Point2D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {
    private final KdTree kt = new KdTree();
    Point2D p = new Point2D(.5, .5);
    @BeforeEach
    void setUp() {
        kt.insert(p);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void draw() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void range() {
    }

    @Test
    void contains() {
                assertAll("KdTree",
                ()-> {boolean result = kt.contains(p);
                assertEquals(result,true);
                assertTrue(result);
        });
        assertEquals(kt.contains(p),true);
    }

    @Test
    void insert() {
    }

    @Test
    void size() {
    }

    @Test
    void nearest() {
    }
}