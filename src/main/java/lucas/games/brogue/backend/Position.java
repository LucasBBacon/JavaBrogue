package lucas.games.brogue.backend;

/**
 * An immutable record representing a 2D coordinate in the dungeon.
 * <p>
 *     This replaces the raw 'int x, int y' parameters passed around in C.
 * </p>
 */
public record Position(int x, int y) {

    /**
     * Returns a new Position offset by dx and dy.
     */
    public Position offset(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    /**
     * Returns the Squared Euclidean distance to another position.
     * Useful for lighting/FOV calculations where square roots should be avoided for comparisons.
     */
    public double distanceSquared(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return dx * dx + dy * dy;
    }

    /**
     * Returns the Euclidean distance to another position.
     */
    public double distance(Position other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Returns the Chebyshev distance (King distance).
     * This represents the number of steps to reach the target moving in any of the 8 directions.
     */
    public int chebyshevDistance(Position other) {
        return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
    }

    /**
     * Checks if this position is within the bounds of the grid.
     */
    public boolean isValid(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
