import edu.princeton.cs.algs4.Point2D;
import org.example.KdTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KdTreeTest {
    private final KdTree kt = new KdTree();
    Point2D p = new Point2D(.5, .5);

    @BeforeEach
    void setUp() {
        kt.insert(p);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void put() {
    }

    @Test
    void contains_should_work() {
        assertTrue(kt.contains(p));
    }

}