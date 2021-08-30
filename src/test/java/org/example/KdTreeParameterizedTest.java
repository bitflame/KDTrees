package org.example;

import edu.princeton.cs.algs4.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
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

/* amazing tutorial - everything you want to know about Junit5 parameterized testing
* https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-parameterized-tests/ Everything! */
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


    Stream<Arguments> createKdTreeInstance() throws IOException {
        Stream<String> stream;
        System.out.println("Inside the createdKdTreeInstance Method ");
        stream = Files.lines(Paths.get("src/main/resources/distinctpoints.txt"));
        kt = new KdTree();
        stream.map(x -> x.split("\\s")).forEach(x -> kt.insert(new Point2D(Double.parseDouble(x[0]),
                Double.parseDouble(x[1]))));
    }

    @DisplayName("range() Method Test")
    //@ParameterizedTest
    @CsvSource({".082,0.5,0.084,0.52"})
    @MethodSource("createKdTreeInstance")
    //@ArgumentsSource(KdTreeArgumentProvider.class)

    void range(double a, double b, double c, double d) throws IOException {
        createKdTreeInstance();
        RectHV r = new RectHV(a, b, c, d);
        Assertions.assertNotNull(kt.range(r));
        // kt.range(r);
        //System.out.println(kt.range(r));
    }
}