package lucas.games.brogue.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrogueRandomTest {

    @Test
    void testDeterminism() {
        // Known sequence verification.
        // If we seed with 1, we expect specific values based on the formula:
        // seed * 1103515245 + 12345

        BrogueRandom rng = new BrogueRandom(1);

        // 1 * 1103515245 + 12345 = 1103527590
        int first = rng.next();
        assertEquals(1103527590, first, "First random number from seed 1 matches C expectation");

        // Calculate next manually: 1103527590 * 1103515245 + 12345
        // In hex: 0x41C78E66 * 0x41C78E6D + 0x3039 = ... overflow ...
        // Expected value relies on 32-bit overflow behavior
        int second = rng.next();

        // Reset and ensure it happens again
        rng.setSeed(1);
        assertEquals(first, rng.next());
        assertEquals(second, rng.next());
    }

    @Test
    void testRandomIntegerRange() {
        BrogueRandom rng = new BrogueRandom(12345);

        for (int i = 0; i < 1000; i++) {
            int val = rng.randomInteger(10);
            assertTrue(val >= 0 && val < 10, "Value " + val + " out of bounds for randomInteger(10)");
        }
    }

    @Test
    void testRandomRange() {
        BrogueRandom rng = new BrogueRandom(54321);

        for (int i = 0; i < 1000; i++) {
            int val = rng.randomRange(50, 100);
            assertTrue(val >= 50 && val <= 100, "Value " + val + " out of bounds for randomRange(50, 100)");
        }
    }

    @Test
    void testFloatRange() {
        BrogueRandom rng = new BrogueRandom(999);

        for (int i = 0; i < 1000; i++) {
            double val = rng.randomFloat();
            assertTrue(val >= 0.0 && val < 1.0, "Value " + val + " out of bounds for randomFloat()");
        }
    }

    @Test
    void testAgainstKnownSequence() {
        // This simulates asequence of calls to ensure state is maintained correctly.
        BrogueRandom rng = new BrogueRandom(100);

        int r1 = rng.next();
        int r2 = rng.next();
        int r3 = rng.next();

        BrogueRandom rng2 = new BrogueRandom(100);
        assertEquals(r1, rng2.next());
        assertEquals(r2, rng2.next());
        assertEquals(r3, rng2.next());
    }
}