package org.example;

import edu.princeton.cs.algs4.Point2D;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;


import static org.junit.jupiter.api.Assertions.*;

class PointInfoAggregator implements ArgumentsAggregator {

    @Override
    public Point2D aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) {
        return new Point2D(arguments.getDouble(0),
                arguments.getDouble(1));
    }
}
class KdTreeParameterizedTest {

    private KdTree kt = new KdTree();
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
    @Test
    void myTest() {
        Point2D p1 = new Point2D(0.5,0.25);
        kt.insert(p1);
        Point2D p2 = new Point2D(0.0,0.5);
        kt.insert(p2);
        Point2D p3 = new Point2D(0.5,0.0);
        kt.insert(p3);
        Point2D p4 = new Point2D(0.25,0.0);
        kt.insert(p4);
        Point2D p5 = new Point2D(0.0,1.0);
        kt.insert(p5);
        Point2D p6 = new Point2D(1.0,0.5);
        kt.insert(p6);
        Point2D p7 = new Point2D(0.25,0.0);
        kt.insert(p7);
        Point2D p8 = new Point2D(0.0,0.25);
        kt.insert(p8);
        Point2D p9 = new Point2D(0.25,0.0);
        kt.insert(p9);
        Point2D p10 = new Point2D(0.25,0.5);
        kt.insert(p10);
        Point2D queryPoint = new Point2D(0.75, 0.75);
        //assertFalse(kt.contains(queryPoint));
        //assertFalse(queryPoint.equals(kt.nearest(queryPoint)));
        //assertEquals(kt.nearest(queryPoint).distanceSquaredTo(queryPoint),0.125);
    }
}