package org.example;

import edu.princeton.cs.algs4.Point2D;
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
class KdTreeTest {

    private KdTree kt = new KdTree();

    @ParameterizedTest
    @CsvSource({"0.5,0.25", "0.0,0.0", "0.5,0.0", "0.5,0.0", "0.25,0.0", "0.0,1.0", "1.0,0.5",
            "1.0,0.5", "0.25,0.0", "0.0,0.25", "0.25,0.0", "0.25,0.5"})
    void nearest(@AggregateWith(PointInfoAggregator.class) Point2D point) {
        kt.insert(point);
        Point2D queryPoint = new Point2D(0.75, 0.75);
        assertFalse(kt.contains(queryPoint));
        assertFalse(queryPoint.equals(kt.nearest(queryPoint)));
    }
}