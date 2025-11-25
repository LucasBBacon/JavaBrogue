package lucas.games.brogue.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testOffset() {
        Position p = new Position(10, 10);
        Position p2 = p.offset(1, -1);

        assertEquals(11, p2.x());
        assertEquals(9, p2.y());
        // Ensure immutability
        assertEquals(10, p.x());
    }

    @Test
    void testEuclideanDistance() {
        Position p1 = new Position(0, 0);
        Position p2 = new Position(3, 4);

        assertEquals(5.0, p1.distance(p2), 0.001);
        assertEquals(25.0, p1.distanceSquared(p2), 0.001);
    }

    @Test
    void testChebyshevDistance() {
        // King move distance
        Position p1 = new Position(0, 0);
        Position p2 = new Position(2, 5);

        // To get from 0,0 to 2,5 you need 5 steps (mostly diagonal)
        assertEquals(5, p1.chebyshevDistance(p2));
    }

    @Test
    void testBounds() {
        Position p = new Position(5, 5);
        assertTrue(p.isValid(10, 10));
        assertFalse(p.isValid(5, 5)); // 0 indexed, so 5 is outside bounds of size
        assertFalse(p.offset(-6, 0).isValid(10, 10));
    }
}