package org.example;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {
    private final KdTree kt = new KdTree();
    Point2D p1 = new Point2D(.5, .5);
    Point2D p2 = new Point2D(.5, .5);
    Point2D p3 = new Point2D(.5, .5);
    Point2D p4 = new Point2D(.5, .5);
    Point2D p5 = new Point2D(.5, .5);
    Point2D p6 = new Point2D(.5, .5);
    Point2D p7 = new Point2D(.5, .5);
    Point2D p8 = new Point2D(.5, .5);
    Point2D p9 = new Point2D(.5, .5);
    Point2D p10 = new Point2D(.5, .5);

    @BeforeEach
    void setUp() {
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

    @ParameterizedTest
    @CsvSource({"0.5,0.25", "0.0,0.0", "0.5,0.0", "0.5,0.0", "0.25,0.0","0.0,1.0","1.0,0.5",
    "1.0,0.5","0.25,0.0","0.0,0.25","0.25,0.0","0.25,0.5","0.75,0.75"})
    void contains(ArgumentsAccessor argumentsAccessor) {
        Point2D p = new Point2D(argumentsAccessor.getDouble(0), argumentsAccessor.getDouble(1));
        kt.insert(p);
        assertTrue(kt.contains(p));
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