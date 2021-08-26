package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;


class KdTreeParameterizedTest {

    KdTree kt = new KdTree();
    @ParameterizedTest
    @CsvFileSource(resources = "/distinctpoints.txt", delimiter = ' ')
    void init(double x, double y) {
        Point2D p = new Point2D(x, y);
        kt.insert(p);
    }


    @ParameterizedTest
    @CsvSource({"0.61,0.31"})
    void containsShouldNotWork(double x, double y) {
        //init();
        Point2D point = new Point2D(x, y);
        Assertions.assertFalse(kt.contains(point));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({"0.0000000,0.000000", "0.0000000,0.500000", "0.5000000,0.000000", "0.6100000,0.300000"})
    void containsShouldWork(double x, double y) {
        Point2D point = new Point2D(x, y);
        //Assertions.assertTrue(kt.contains(point));
    }


    @ParameterizedTest
    @CsvSource({".082,0.5,0.084,0.52"})
    void range(double a, double b, double c, double d) {
        RectHV r = new RectHV(a, b, c, d);
        kt.range(r);
        System.out.println(kt.range(r));
    }
}