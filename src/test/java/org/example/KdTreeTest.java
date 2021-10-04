package org.example;

import edu.princeton.cs.algs4.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {
KdTree kd = new KdTree();
    @ParameterizedTest
    @CsvFileSource(resources = "input10K.txt", delimiterString = " ")
    void nearest(double x, double y) {
        kd.insert(new Point2D(x, y));
    }
}