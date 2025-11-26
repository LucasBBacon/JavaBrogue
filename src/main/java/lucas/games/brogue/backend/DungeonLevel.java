package lucas.games.brogue.backend;

/**
 * Represents a single depth of the dungeon.
 * Contains the grid of tiles nad manages spatial lookups.
 */
public class DungeonLevel {

    private final int width;
    private final int height;
    private final Tile[][] grid;

    /**
     * Creates a new empty dungeon level filled with Walls by default.
     * Standard Brogue size is typically 100x40.
     */
    public DungeonLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];

        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // default to solid wall
                grid[x][y] = new Tile(TerrainType.WALL);
            }
        }
    }

    /**
     * Wipes the level clean
     * Replaces every tile with a fresh wall tile, removing all items, occupants, etc.
     */
    public void reset() {
        initializeGrid();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Safely retrieves a tile at the given coordinates.
     * Returns null (or potentially a "Void" tile object future imp) if out of bounds.
     */
    public Tile getTile(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return null;
        }
        return grid[x][y];
    }

    public Tile getTile(Position pos) {
        return getTile(pos.x(), pos.y());
    }

    /**
     * Sets a tile at a specific location.
     */
    public void setTile(int x, int y, Tile tile) {
        if (isValidCoordinate(x, y)) {
            grid[x][y] = tile;
        }
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isValidCoordinate(Position pos) {
        return isValidCoordinate(pos.x(), pos.y());
    }

    /**
     * Prepares the level for a new turn (clears temp lighting, effects, etc).
     */
    public void prepareTurn() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].resetForTurn();
            }
        }
    }
}
