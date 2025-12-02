package lucas.games.brogue.backend;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Grid {

    private final int cols;
    private final int rows;
    private final Tile[][] tiles;

    private Grid(Builder builder) {
        this.cols = builder.cols;
        this.rows = builder.rows;
        this.tiles = builder.tiles;
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public static Builder builder() {
        return new Builder();
    }

    public Tile getTile(int col, int row) {
        return tiles[row][col];
    }

    public boolean isInBounds(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return Objects.deepEquals(tiles, grid.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }

    @Override
    public String toString() {
        if (tiles == null) return super.toString();

        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Tile tile = tiles[row][col];
                sb.append(tile == null ? "null" : tile.type().name());
                if (col < cols - 1) sb.append(' ');
            }
            if (row < rows - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public static class Builder {

        public static final int DEFAULT_COLS = 20;
        public static final int DEFAULT_ROWS = 10;

        private int cols = DEFAULT_COLS;
        private int rows = DEFAULT_ROWS;
        private Tile[][] tiles;

        public Builder withDimensions(int cols, int rows) {
            this.cols = cols;
            this.rows = rows;
            return this;
        }

        public Builder withTiles(Tile[][] tiles) {
            this.tiles = tiles;
            this.rows = tiles.length;
            this.cols = tiles[0].length;
            return this;
        }

        private void initializeGrid() {
            this.tiles = new Tile[rows][cols];
            for (int row = 0; row < this.rows; row++) {
                for (int col = 0; col < this.cols; col++) {
                    this.tiles[row][col] = new Tile();
                }
            }
        }

        public Grid build() {
            if (tiles == null) {
                initializeGrid();
            }
            return new Grid(this);
        }
    }

    public Edit edit() {
        return new Edit(this);
    }

    public static class Edit {

        private int cols;
        private int rows;
        private Tile[][] tiles;

        private Edit(Grid grid) {
            copy(grid);
        }

        private boolean isInBounds(int col, int row) {
            return col >= 0 && col < cols && row >= 0 && row < rows;
        }

        public Edit copy(Grid grid) {
            this.cols = grid.cols;
            this.rows = grid.rows;
            this.tiles = new Tile[this.rows][this.cols];
            for (int row = 0; row < this.rows; row++) {
                for (int col = 0; col < this.cols; col++) {
                    this.tiles[row][col] = grid.getTile(col, row);
                }
            }
            return this;
        }

        public Edit fill(Tile.TileType value) {
            requireNonNull(value, "TileType value is required");

            for (int row = 0; row < this.rows; row++) {
                for (int col = 0; col < this.cols; col++) {
                    this.tiles[row][col] = new Tile(value);
                }
            }
            return this;
        }

        public Edit findReplace(Tile.TileType findValue, Tile.TileType fillValue) {
            requireNonNull(findValue, "TileType to find is required");
            requireNonNull(fillValue, "TileType to fill is required");

            for (int row = 0; row < this.rows; row++) {
                for (int col = 0; col < this.cols; col++) {
                    if (this.tiles[row][col].type() == findValue) {
                        this.tiles[row][col] = new Tile(fillValue);
                    }
                }
            }
            return this;
        }

        public Edit floodFill(int col, int row, Tile.TileType targetValue, Tile.TileType fillValue) {
            requireNonNull(targetValue, "TileType to replace is required");
            requireNonNull(fillValue, "TileType to fill is required");

            if (!this.isInBounds(col, row)) return this;

            if (this.tiles[row][col].type() != targetValue || targetValue == fillValue) return this;

            int[][] dirs = { {1,0}, {-1,0}, {0,1}, {0,-1} };

            this.tiles[row][col] = new Tile(fillValue);

            for (int[] dir: dirs) {
                int nextRow = row + dir[1];
                int nextCol = col + dir[0];
                if (nextRow >= 0 && nextRow < rows && nextCol >= 0 && nextCol < cols) {
                    Tile.TileType nextType = this.tiles[nextRow][nextCol].type();
                    if (nextType == targetValue) {
                        floodFill(nextCol, nextRow, targetValue, fillValue);
                    }
                }
            }
            return this;
        }

        public Edit drawRectangle(int col, int row, int width, int height, Tile.TileType fillValue) {
            requireNonNull(fillValue, "TileType to draw is required");
            if (!isInBounds(col, row)) return this;
            if (width <= 0 || height <= 0) return this;

            for (int r = row; r < row + height; r++) {
                for (int c = col; c < col + width; c++) {
                    if (isInBounds(c, r) && this.tiles[r][c].type() != fillValue) {
                        this.tiles[r][c] = new Tile(fillValue);
                    }
                }
            }
            return this;
        }

        public Edit drawCircle(int col, int row, int radius, Tile.TileType fillValue) {
            requireNonNull(fillValue, "TileType to draw is required");
            if (!isInBounds(col, row)) return this;
            if (radius <= 0) return this;

            for (int r = Math.max(0, row - radius); r <= Math.min(rows - 1, row + radius); r++) {
                for (int c = Math.max(0, col - radius); c <= Math.min(cols - 1, col + radius); c++) {
                    int dx = c - col;
                    int dy = r - row;
                    if ((dx * dx + dy * dy <= radius * radius) && this.tiles[r][c].type() != fillValue) {
                        this.tiles[r][c] = new Tile(fillValue);
                    }
                }
            }
            return this;
        }

        public Grid build() {
            return Grid.builder().withTiles(tiles).build();
        }
    }
}
