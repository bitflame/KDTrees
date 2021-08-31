package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;


class KdTreeParameterizedTest {
    static class KdTreeArgumentsProvider implements ArgumentsProvider {

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
            KdTree kt = new KdTree();
            Point2D p = new Point2D(0.372, 0.497);
            kt.insert(p);
            p = new Point2D(0.564, 0.413);
            kt.insert(p);
            p = new Point2D(0.226, 0.577);
            kt.insert(p);
            p = new Point2D(0.144, 0.179);
            kt.insert(p);
            p = new Point2D(0.083, 0.51);
            kt.insert(p);
            p = new Point2D(0.32, 0.708);
            kt.insert(p);
            p = new Point2D(0.417, 0.362);
            kt.insert(p);
            p = new Point2D(0.862, 0.825);
            kt.insert(p);
            p = new Point2D(0.785, 0.725);
            kt.insert(p);
            p = new Point2D(0.499, 0.208);
            kt.insert(p);
            // here is how to return KdTree instance and the rectangle.
            // return Stream.of(kt, r, expectPoints).map(Arguments::of); -- 08/08/21 13:24
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