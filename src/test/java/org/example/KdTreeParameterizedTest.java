package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
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
                String fileName = fileEntry.getName().toUpperCase();
                if (fileName.endsWith(".TXT")) {
                    Scanner scanner = new Scanner(fileEntry);
                    kt = new KdTree();
                    while (scanner.hasNext()) {
                        double x = scanner.nextDouble();
                        double y = scanner.nextDouble();
                        Point2D p = new Point2D(x, y);
                        kt.insert(p);
                    }
                    instanceData[i++] = kt;
                    instanceData[i++] = r;
                    instanceData[i++] = expectedPoints;
                    if (i > instanceDataLength) instanceData = resizeArray(2 * 3, instanceData);
                }
                // System.out.println("Done with file " + fileName + " value of i is: " + i);
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
    @ParameterizedTest(name = "{index}=> kt={0},r={1},expectedPoints={2}")
    @ArgumentsSource(KdTreeArgumentsProvider.class)
    void range(KdTree kt, RectHV r, Point2D[] expectedPoints) {
        System.out.println("running the test for tree size of: "+kt.size());
        //Assertions.assertNotNull(kt.range(r));
        Assertions.assertEquals(kt.size(),100);
    }
}