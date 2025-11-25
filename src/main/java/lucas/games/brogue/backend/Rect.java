package lucas.games.brogue.backend;

/**
 * Represents a rectangular are in the dungeon.
 * Used for room generation and collision detection.
 */
public record Rect(int x, int y, int width, int height) {

    public int x2() { return x + width; }
    public int y2() { return y + height; }

    public Position getCenter() {
        return new Position(x + width / 2, y + height / 2);
    }

    /**
     * Returns true if this rectangle intersects with the other.
     * Used to prevent rooms from overlapping messily.
     */
    public boolean intersects(Rect other) {
        return (this.x <= other.x + other.width && this.x + this.width >= other.x &&
                this.y <= other.y + other.height && this.y + this.height >= other.y);
    }

    /**
     * Returns a random position inside this rectangle.
     */
    public Position getRandomPosition(BrogueRandom rng) {
        int px = rng.randomRange(x, x + width - 1);
        int py = rng.randomRange(y, y + height - 1);
        return new Position(px, py);
    }
}
