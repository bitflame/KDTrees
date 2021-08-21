package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;


import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PointInfoAggregator implements ArgumentsAggregator {

    @Override
    public Point2D aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) {
        return new Point2D(arguments.getDouble(0),
                arguments.getDouble(1));
    }
}

class RectangleInfoAggregator implements ArgumentsAggregator {
    @Override
    public RectHV aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) {
        return new RectHV(arguments.getDouble(0), arguments.getDouble(1), arguments.getDouble(2),
                arguments.getDouble(3));
    }
}

class KdTreeParameterizedTest {

    KdTree kt = new KdTree();

    @Disabled
    @BeforeEach
    void init() {
        In in = new In();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kt.insert(p);
        }
        double x = StdRandom.uniform(0.0, 1.0);
        double y = StdRandom.uniform(0.0, 1.0);
        Point2D p = new Point2D(x, y);
        for (int i = 0; i < 10; i++) kt.insert(p);
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({"0.61,0.31"})
    void containsShouldNotWork(double x, double y) {
        Point2D point = new Point2D(x, y);
        Assertions.assertFalse(kt.contains(point));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({"0.0000000,0.000000", "0.0000000,0.500000", "0.5000000,0.000000", "0.6100000,0.300000"})
    void containsShouldWork(double x, double y) {
        Point2D point = new Point2D(x, y);
        Assertions.assertTrue(kt.contains(point));
    }

    /* This is not exactly what I wanted to do, but it is a good example that I can follow as a
     * reference */
    @Disabled
    @ParameterizedTest
    @CsvSource({"0.5,0.25", "0.0,0.0", "0.5,0.0", "0.5,0.0", "0.25,0.0", "0.0,1.0", "1.0,0.5",
            "1.0,0.5", "0.25,0.0", "0.0,0.25", "0.25,0.0", "0.25,0.5"})
    void nearest(@AggregateWith(PointInfoAggregator.class) Point2D point) {
        kt.insert(point);
        Point2D queryPoint = new Point2D(0.75, 0.75);
        assertFalse(kt.contains(queryPoint));
        assertFalse(queryPoint.equals(kt.nearest(queryPoint)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/distinctpoints.txt", delimiter = ' ')
    void range(@AggregateWith(RectangleInfoAggregator.class) RectHV r) {
        In in = new In();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kt.insert(p);
        }
        // kt.range(r);
    }
}