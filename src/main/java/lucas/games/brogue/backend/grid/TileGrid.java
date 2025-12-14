package lucas.games.brogue.backend.grid;

import io.vavr.Function1;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.Random;
import lucas.games.brogue.backend.grid.cells.TerrainFlags;
import lucas.games.brogue.backend.grid.cells.WorldCell;

import java.util.LinkedList;
import java.util.Queue;

import static java.util.Objects.requireNonNull;
import static lucas.games.brogue.backend.grid.Constants.COLS;
import static lucas.games.brogue.backend.grid.Constants.ROWS;

public class TileGrid extends Grid<WorldCell> {

    private final Random random;

    protected TileGrid(final int cols, final int rows, final Vector<WorldCell> cells) {
        super(cols, rows, cells);
        this.random = new Random();
    }

    @Override
    protected Grid<WorldCell> newInstance(final Vector<WorldCell> newCells) {
        return new TileGrid(this.cols, this.rows, newCells);
    }

    // =================================================================================================================
    // STATIC FACTORIES
    // =================================================================================================================

    public static TileGrid defaultGrid() {
        return filled(COLS, ROWS, WorldCell.EMPTY);
    }

    public static TileGrid empty(final int cols, final int rows) {
        return new TileGrid(cols, rows, Vector.empty());
    }

    public static TileGrid of(final int cols, final int rows, final WorldCell... values) {
        return new TileGrid(cols, rows, Vector.of(values));
    }

    public static TileGrid of(final int cols, final int rows, final Vector<WorldCell> values) {
        return new TileGrid(cols, rows, values);
    }

    public static TileGrid filled(final int cols, final int rows, final WorldCell fillValue) {
        requireNonNull(fillValue, "Fill value must not be null");
        return new TileGrid(cols, rows, Vector.fill(cols * rows, fillValue));
    }

    // =================================================================================================================
    // COVARIANT OVERRIDES
    // =================================================================================================================

    @Override
    public TileGrid set(final int col, final int row, final WorldCell value) {
        return (TileGrid) super.set(col, row, value);
    }

    @Override
    public TileGrid set(final Position position, final WorldCell value) {
        return (TileGrid) super.set(position, value);
    }

    @Override
    public TileGrid updateAll(final Function1<WorldCell, WorldCell> mapper) {
        return (TileGrid) super.updateAll(mapper);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(getRow(i).map(cell -> cell.memoryTerrain().symbol()).mkString("[", ", ", "]"))
                    .append("\n");
        }
        return sb.toString();
    }

    // =================================================================================================================
    // TYPE-SPECIFIC METHODS
    // =================================================================================================================

    public boolean cellHasFlag(final int col, final int row, final int blockingFlags) {
        if (!isInBounds(col, row)) return false;
        return this.get(col, row).hasFlag(blockingFlags);
    }

    public boolean cellHasFlags(final int col, final int row, final int... blockingFlags) {
        if (!isInBounds(col, row)) return false;
        return this.get(col, row).hasFlags(blockingFlags);
    }

    public boolean cellHasMechanicFlags(final int col, final int row, final int mechanicsFlag) {
        if (!isInBounds(col, row)) return false;
        return this.get(col, row).tileHasMechanicFlag(mechanicsFlag);
    }

    public boolean cellHasMechanicFlags(final int col, final int row, final int... mechanicsFlags) {
        if (!isInBounds(col, row)) return false;
        return this.get(col, row).tileHasMechanicFlags(mechanicsFlags);
    }

    public Option<Position> getQualifyingPathLocNear(final Position startPos,
                                                     final boolean hallwaysAllowed,
                                                     final int blockingTerrainFlags,
                                                     final int blockingMapFlags,
                                                     final int forbiddenTerrainFlags,
                                                     final int forbiddenMapFlags,
                                                     final boolean deterministic) {
        if (!isInBounds(startPos)) {
            return Option.none();
        }

        boolean[][] visited = new boolean[rows][cols];
        Queue<Position> queue = new LinkedList<>();

        queue.add(startPos);
        visited[startPos.row()][startPos.col()] = true;

        int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        while (!queue.isEmpty()) {
            System.out.println(queue);

            Position current = queue.poll();
            WorldCell cell = this.get(current);

            boolean isForbidden = cell.tileHasTerrainFlags(forbiddenTerrainFlags) || cell.hasFlags(forbiddenMapFlags);
            if (isForbidden) {
                continue;
            }

            boolean isBlocking = cell.tileHasTerrainFlags(blockingTerrainFlags) || cell.hasFlags(blockingMapFlags);
            if (!isBlocking && !hallwaysAllowed) {
                if (getPassableArcCount(current.col(), current.row()) == 2) {
                    isBlocking = true;
                }
            }

            if (!isBlocking) {
                return Option.of(current);
            }

            if (!deterministic) {
                shuffleOffsets(offsets);
            }

            for (int[] offset : offsets) {
                int nCol = current.col() + offset[1];
                int nRow = current.row() + offset[0];

                if (isInBounds(nCol, nRow) && !visited[nRow][nCol]) {
                    visited[nRow][nCol] = true;
                    queue.add(new Position(nCol, nRow));
                }
            }
        }

        return Option.none();
    }

    private int getPassableArcCount(final int col, final int row) {
        int transitions = 0;

        int wallFlag = TerrainFlags.OBSTRUCTS_PASSABILITY;

        for (int i = 0; i < Constants.C_DIRS.length; i++) {
            boolean currPass = isPassableTopology(
                    col + Constants.C_DIRS[i][0],
                    row + Constants.C_DIRS[i][1],
                    wallFlag);
            boolean prevPass = isPassableTopology(
                    col + Constants.C_DIRS[(i + 7) % Constants.C_DIRS.length][0],
                    row + Constants.C_DIRS[(i + 7) % Constants.C_DIRS.length][1],
                    wallFlag);

            if (currPass != prevPass) {
                transitions++;
            }
        }
        return transitions / 2;
    }

    private boolean isPassableTopology(final int col, final int row, int wallFlag) {
        if (!isInBounds(col, row)) {
            return false;
        }
        return !get(col, row).tileHasTerrainFlags(wallFlag);
    }

    private void shuffleOffsets(int[][] offsets) {
        for (int i = offsets.length - 1; i > 0; i--) {
            int index = random.randomInt(i + 1);
            int[] temp = offsets[index];
            offsets[index] = offsets[i];
            offsets[i] = temp;
        }
    }

    // =================================================================================================================
    // GRID GENERATORS
    // =================================================================================================================

    public IntGrid getTerrainGrid(final IntGrid intGrid,
                                  final int value,
                                  final int terrainFlags,
                                  final int mapFlags) {
        Vector<Integer> intVector = this.cells.zipWithIndex().map(tuple -> {
            WorldCell cell = tuple._1();
            int index = tuple._2;
            Integer currentIntGridCell = intGrid.get(index % cols, index / cols);
            return (currentIntGridCell != value
                    && cell.tileHasTerrainFlags(terrainFlags) || cell.hasFlags(mapFlags))
                    ? value
                    : currentIntGridCell;
        });
        return IntGrid.of(this.cols, this.rows, intVector);
    }

    public IntGrid getTerrainMechGrid(final IntGrid intGrid, final int value, final int... terrainMechFlags) {
        Vector<Integer> intVector = this.cells.zipWithIndex()
                .map(tuple -> {
                    WorldCell cell = tuple._1;
                    int index = tuple._2;
                    Integer currentIntGridCell = intGrid.get(index % cols, index / cols);
                    return (currentIntGridCell != value && cell.tileHasMechanicFlags(terrainMechFlags))
                            ? value
                            : currentIntGridCell;
                });
        return IntGrid.of(this.cols, this.rows, intVector);
    }

    // =================================================================================================================
    // EDITOR
    // =================================================================================================================

    public Edit edit() {
        return new Edit(this);
    }

    public static class Edit {

        private int cols;
        private int rows;
        private Vector<WorldCell> cells;

        private Edit(TileGrid tileGrid) {
            this.cols = tileGrid.cols;
            this.rows = tileGrid.rows;
            this.cells = tileGrid.cells;
        }

        private boolean isInBounds(int col, int row) {
            return col >= 0 && col < cols && row >= 0 && row < rows;
        }


        public Edit fill(WorldCell tile) {
            requireNonNull(tile, "Tile value is required");

            this.cells = Vector.fill(this.cols * this.rows, tile);
            return this;
        }

        public TileGrid build() {
            return TileGrid.of(this.cols, this.rows, this.cells);
        }
    }
}
