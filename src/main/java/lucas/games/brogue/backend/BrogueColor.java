package lucas.games.brogue.backend;

import java.util.Objects;

/**
 * Represents a floating-point colour used for Brogue's lighting engine.
 * Unlike standard AWT colours (integers 0-255), Brogue calculations happen
 * in a floating point space allowing for smooth lighting gradients, over-exposure,
 * and light blending.
 *
 * This class is immutable.
 */
public final class BrogueColor {

    // Standard Brogue Palette constants
    public static final BrogueColor BLACK = new BrogueColor(0.0, 0.0, 0.0);
    public static final BrogueColor WHITE = new BrogueColor(1.0, 1.0, 1.0);
    public static final BrogueColor RED   = new BrogueColor(1.0, 0.0, 0.0);
    public static final BrogueColor BLUE  = new BrogueColor(0.0, 0.0, 1.0);
    public static final BrogueColor TEAL  = new BrogueColor(0.0, 1.0, 1.0);
    public static final BrogueColor TORCH_LIGHT = new BrogueColor(1.0, 0.9, 0.7);

    private final double r;
    private final double g;
    private final double b;

    public BrogueColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Constructs a BrogueColor from standard 0-255 RBG integers.
     */
    public static BrogueColor fromRgb(int r, int g, int b) {
        return new BrogueColor(r / 255.0, g / 255.0, b / 255.0);
    }

    /**
     * Returns a new color that is a linear interpolation between this color and the target color.
     * Used extensively in Brogue for lighting falloff and atmospheric blending.
     * @param target The color to blend towards.
     * @param percent The percentage of the target color (0.0 = this, 1.0 = target).
     * @return A new blended BrogueColor.
     */
    public BrogueColor lerp(BrogueColor target, double percent) {
        double newR = this.r + (target.r - this.r) * percent;
        double newG = this.g + (target.g - this.g) * percent;
        double newB = this.b + (target.b - this.b) * percent;
        return new BrogueColor(newR, newG, newB);
    }

    /**
     * Scales the color brightness. Used for dimming lights.
     */
    public BrogueColor scale(double factor) {
        return new BrogueColor(this.r * factor, this.g * factor, this.b * factor);
    }

    /**
     * Adds two colors together. Used when multiple light sources hit the same tile.
     */
    public BrogueColor add(BrogueColor other) {
        return new BrogueColor(this.r + other.r, this.g + other.g, this.b + other.b);
    }

    /**
     * Clamps the color values between 0.0 and 1.0.
     * Useful before rendering to screen, but calculations often exceed 1.0 (HDR).
     */
    public BrogueColor clamp() {
        return new BrogueColor(
                Math.min(1.0, Math.max(0.0, r)),
                Math.min(1.0, Math.max(0.0, g)),
                Math.min(1.0, Math.max(0.0, b))
        );
    }

    // Getters
    public double red() { return this.r; }
    public double green() { return this.g; }
    public double blue() { return this.b; }

    /**
     * Helper to convert to a packed integer for rendering libraries.
     * Format: 0xRRGGBB
     */
    public int toRgbInt() {
        BrogueColor clamped = this.clamp();
        int rInt = (int) (clamped.r * 255.0);
        int gInt = (int) (clamped.g * 255.0);
        int bInt = (int) (clamped.b * 255.0);
        return (rInt << 16) | (gInt << 8) | bInt;
    }

    @Override
    public String toString() {
        return String.format("Color[%.2f, %.2f, %.2f]", r, g, b);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BrogueColor that = (BrogueColor) o;
        return Double.compare(r, that.r) == 0 && Double.compare(g, that.g) == 0 && Double.compare(b, that.b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
