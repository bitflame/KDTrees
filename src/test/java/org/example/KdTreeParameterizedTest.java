package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;


class KdTreeParameterizedTest {
    static class KdTreeArgumentsProvider implements ArgumentsProvider {
        final static File folder = new File("src/test/resources/kdtests/");
        // read the data for each test from the files in a directory that contains the points, rectangle(s), and the expected result(s)
        // create the kdtree and add points to it by reading two double values from a file. I wonder how I should design the
        // input data file. Ideally I want all the points first, then I want to create a rectangle, but it would be nice to
        // have one file that results in multiple test instances with the same KdTree but different rectangles and expected
        // results
        KdTree kt = new KdTree();
        // create a rectangle by reading four double values from a file. May be possible to put the data in the same file
        // that way one file has all the data for one test instance
        RectHV r = new RectHV(0.1, 0.1, 0.5, 0.6);
        // Just adding two points to expected points for now, but it can be expanded.
        Point2D p1 = new Point2D(0.1, 0.2);
        Point2D p2 = new Point2D(0.8, 0.9);
        Point2D[] expectedPoints = {p1, p2};

        // pass the tree instance and the rectangle along with the expected results to a test method to validate the result
        // the expected results are a set of Points
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            int i = 0;
            int instanceDataLength = 300;
            Object[] instanceData = new Object[instanceDataLength];
            for (final File fileEntry : folder.listFiles()) {
                Point2D[] expectedPoints = new Point2D[10];
                String fileName = fileEntry.getName().toUpperCase();
                if (fileName.endsWith(".TXT")) {
                    Scanner scanner = new Scanner(fileEntry);
                    KdTree kt = new KdTree();
                    RectHV r = null;
                    // read the kdtree points coordinates
                    String s = "";

                    while (scanner.hasNext() && (!(s = scanner.next()).equals("|"))) {
                        double x = Double.parseDouble(s);
                        double y = scanner.nextDouble();
                        Point2D p = new Point2D(x, y);
                        kt.insert(p);
                    }
                    // read the rectangle coordinates
                    // scanner.skip("|");
                    s = "";
                    while (scanner.hasNext() && (!(s = scanner.next()).equals("|"))) {
                        double xmin = Double.parseDouble(s);
                        double ymin = scanner.nextDouble();
                        double xmax = scanner.nextDouble();
                        double ymax = scanner.nextDouble();
                        r = new RectHV(xmin, ymin, xmax, ymax);
                    }
                    // scanner.skip("|");
                    int j = 0;
                    while (scanner.hasNext()) {
                        double x = scanner.nextDouble();
                        double y = scanner.nextDouble();
                        Point2D p = new Point2D(x, y);
                        expectedPoints[j++] = p;
                        if (j > (expectedPoints.length / 2)) resizeArray((2 * expectedPoints.length),
                                expectedPoints);
                    }
                    if (i > (instanceData.length / 2))
                        instanceData = resizeArray((2 * instanceData.length), instanceData);
                    instanceData[i++] = fileEntry.getName();
                    instanceData[i++] = kt;
                    instanceData[i++] = r;
                    instanceData[i++] = expectedPoints;
                }
            }
            return Stream.of(Arguments.of(instanceData));
        }

        Object[] resizeArray(int newSize, Object[] oldInstanceDataArray) {
            Object[] newArray = new Object[newSize];
            for (int i = 0; i < oldInstanceDataArray.length; i++) {
                newArray[i] = oldInstanceDataArray[i];
            }
            return newArray;
        }
    }
    @DisplayName("should create a rectangle with the given coordinates and test KdTree's range() function")
    @ParameterizedTest(name = "{index}=> file: {0}, rectangle ={2}, expectedPoints = {3}")
    @ArgumentsSource(KdTreeArgumentsProvider.class)
    void range(String filename, KdTree kdtree, RectHV rect, Point2D[] expectedPoints) throws IOException {
        HashMap<Integer, Point2D> actualRange = new HashMap<>();
        int i = 0;
        for (Point2D p : kdtree.range(rect)) {
            actualRange.put(i++, p);
        }
        for (Point2D p : expectedPoints) {
            if (p != null) Assertions.assertTrue(actualRange.containsValue(p) && i == actualRange.size());
        }
    }
}