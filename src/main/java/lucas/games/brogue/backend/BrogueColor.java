package lucas.games.brogue.backend;

import java.util.Objects;

public final class BrogueColor {

    private final double r;
    private final double g;
    private final double b;

    public BrogueColor(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Convert RGB values (0-255) to BrogueColor (0.0-1.0)
     */
    public BrogueColor fromRgb(int r, int g, int b) {
        return new BrogueColor(r / 255.0, g / 255.0, b / 255.0);
    }

    /**
     * Linearly interpolate between this color and the target color by the given percent.
     *
     * @param target  The color to blend towards.
     * @param percent The percentage to blend (0.0 - 1.0).
     * @return The blended color.
     */
    public BrogueColor lerp(BrogueColor target, double percent) {
        double newR = this.r + (target.r - this.r) * percent;
        double newG = this.g + (target.g - this.g) * percent;
        double newB = this.b + (target.b - this.b) * percent;
        return new BrogueColor(newR, newG, newB);
    }

    public BrogueColor scale(double factor) {
        return new BrogueColor(this.r * factor, this.g * factor, this.b * factor);
    }

    public BrogueColor add(BrogueColor other) {
        return new BrogueColor(this.r + other.r, this.g + other.g, this.b + other.b);
    }

    public BrogueColor clamp() {
        double clampedR = Math.min(1.0, Math.max(0.0, this.r));
        double clampedG = Math.min(1.0, Math.max(0.0, this.g));
        double clampedB = Math.min(1.0, Math.max(0.0, this.b));
        return new BrogueColor(clampedR, clampedG, clampedB);
    }

    public double red() { return r; }
    public double green() { return g; }
    public double blue() { return b; }

    public int toRgbInt() {
        BrogueColor clamped = this.clamp();
        int rInt = (int) (clamped.r * 255);
        int gInt = (int) (clamped.g * 255);
        int bInt = (int) (clamped.b * 255);
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
        return Double.compare(r, that.r) == 0 &&
               Double.compare(g, that.g) == 0 &&
               Double.compare(b, that.b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}