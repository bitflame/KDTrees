package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.stream.Stream;


class KdTreeParameterizedTest {
    static class KdTreeArgumentsProvider implements ArgumentsProvider {
        final static File folder = new File("src/main/resources/");
        // create a rectangle by reading four double values from a file. May be possible to put the data in the same file
        // that way one file has all the data for one test instance
        RectHV r = new RectHV(0.082, 0.5, 0.084, 0.52);
        // Just adding two points to expected points for now, but it can be expanded.
        Point2D p1 = new Point2D(0.083, 0.51);
        Point2D[] expectPoints = {p1};

        // pass the tree instance and the rectangle along with the expected results to a test method to validate the result
        // the expected results are a set of Points
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            // read the data for each test from the files in a directory that contains the points, rectangle(s), and the expected result(s)
            // create the kdtree and add points to it by reading two double values from a file. I wonder how I should design the
            // input data file. Ideally I want all the points first, then I want to create a rectangle, but it would be nice to
            // have one file that results in multiple test instances with the same KdTree but different rectangles and expected
            // results

            for (final File fileEntry : folder.listFiles()) {
                System.out.println(fileEntry);
            }
            Scanner scanner = new Scanner(new File("src/main/resources/distinctpoints.txt"));
            KdTree kt = new KdTree();
            while (scanner.hasNext()) {
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();
                Point2D p = new Point2D(x, y);
                kt.insert(p);
            }
            return Stream.of(Arguments.of(kt, r, expectPoints));
        }
    }

    @DisplayName("should create a rectangle with the given coordinates and test KdTree's range() function")
    @ParameterizedTest(name = "{index}=> kt={0},rectangle={1},expectedPoints={2}")
    @ArgumentsSource(KdTreeArgumentsProvider.class)
    void range(KdTree kt, RectHV r, Point2D[] expectedPoints) {
        Assertions.assertNotNull(kt.range(r));
        // how do I validate expected points if/when there are more than one?
    }
}