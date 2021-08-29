package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;


class KdTreeParameterizedTest {

    KdTree kt = new KdTree();

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


    static void createKdTreeInstance() {
        Stream<String> stream;
        try {
            // BufferedReader br = new BufferedReader(new FileReader("DistinctPoints.txt"));
            System.out.println("Inside the createdKdTreeInstance Method ");
            stream = Files.lines(Paths.get("src/main/resources/distinctpoints.txt"));
            //stream.forEach(System.out::println);
            // stream.spliterator().tryAdvance(a -> a.split("\\s"))
            //Double xcor = Double.parseDouble(stream.toString());
            //Double ycor = Double.parseDouble(stream.toString());
            KdTree kt = new KdTree();
            stream.map(x -> x.split("\\s")).forEach(x -> kt.insert(new Point2D(Double.parseDouble(x[0]),
                    Double.parseDouble(x[1]))));
            System.out.println("$%%@#$#4");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // return stream;
    }

    @DisplayName("range() Method Test")
    @ParameterizedTest
    // @CsvSource({".082,0.5,0.084,0.52"})
    @MethodSource("createKdTreeInstance")
    void range(double a, double b, double c, double d) {
        //RectHV r = new RectHV(a, b, c, d);
        //kt.range(r);
        //System.out.println(kt.range(r));
    }
}