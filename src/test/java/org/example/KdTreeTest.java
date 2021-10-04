package org.example;

import edu.princeton.cs.algs4.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {
    File fileName = new File("src\\main\\resources\\circlee100.txt");
    KdTree kd = new KdTree();
    private void populateTree(KdTree kt, File newFile) {
        try {
            Scanner scanner = new Scanner(newFile);
            while (scanner.hasNext()) {
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();
                Point2D p = new Point2D(x, y);
                kt.insert(p);
                // System.out.println(kt.rank(p));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    @ParameterizedTest
    @CsvFileSource(resources = "FollowUp.txt", delimiterString = " ")
    void nearest(double x, double y) {
        populateTree(kd,fileName);
        Point2D query = new Point2D(x,y);

    }
}