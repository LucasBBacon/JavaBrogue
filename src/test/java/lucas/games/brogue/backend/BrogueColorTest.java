package lucas.games.brogue.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrogueColorTest {

    @Test
    void testCreationAndGetters() {
        BrogueColor c = new BrogueColor(0.5, 0.2, 0.1);
        assertEquals(0.5, c.red());
        assertEquals(0.2, c.green());
        assertEquals(0.1, c.blue());
    }

    @Test
    void testFromRgb() {
        BrogueColor c = BrogueColor.fromRgb(255, 0, 127);
        assertEquals(1.0, c.red());
        assertEquals(0.0, c.green());
        assertTrue(Math.abs(c.blue() - 0.498) < 0.01); // Float point precision check
    }

    @Test
    void testLerpBlending() {
        BrogueColor c1 = new BrogueColor(0.0, 0.0, 0.0); // Black
        BrogueColor c2 = new BrogueColor(1.0, 1.0, 1.0); // White

        // Blend 50%
        BrogueColor gray = c1.lerp(c2, 0.5);
        assertEquals(0.5, gray.red());
        assertEquals(0.5, gray.green());
        assertEquals(0.5, gray.blue());

        // Blend 10%
        BrogueColor darkGray = c1.lerp(c2, 0.1);
        assertEquals(0.1, darkGray.red(), 0.001);
    }

    @Test
    void testScaling() {
        BrogueColor white = new BrogueColor(1.0, 1.0, 1.0);
        BrogueColor halfBright = white.scale(0.5);

        assertEquals(0.5, halfBright.red());
        assertEquals(0.5, halfBright.green());
        assertEquals(0.5, halfBright.blue());
    }

    @Test
    void testAddAndClamp() {
        BrogueColor red = new BrogueColor(0.8, 0.0, 0.0);
        BrogueColor red2 = new BrogueColor(0.5, 0.0, 0.0);

        // Add allows values > 1.0 (High dynamic range)
        BrogueColor brighRed = red.add(red2);
        assertEquals(1.3, brighRed.red(), 0.001);

        // Clamp brings it back to displayable range
        BrogueColor displayRed = brighRed.clamp();
        assertEquals(1.0, displayRed.red());
    }
}