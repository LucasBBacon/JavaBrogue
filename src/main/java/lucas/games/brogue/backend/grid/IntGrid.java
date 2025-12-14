package lucas.games.brogue.backend.grid;

import io.vavr.Function1;
import io.vavr.collection.Vector;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.Random;

import static java.util.Objects.requireNonNull;
import static lucas.games.brogue.backend.grid.Constants.COLS;
import static lucas.games.brogue.backend.grid.Constants.ROWS;

public class IntGrid extends Grid<Integer> {

    private final Random random = new Random();

    private IntGrid(int cols, int rows, Vector<Integer> cells) {
        super(cols, rows, cells);
    }

    @Override
    protected Grid<Integer> newInstance(Vector<Integer> newCells) {
        return new IntGrid(cols, rows, newCells);
    }

    // =================================================================================================================
    // STATIC FACTORIES
    // =================================================================================================================

    public static IntGrid defaultGrid() {
        return filled(COLS, ROWS, 0);
    }

    public static IntGrid empty(int cols, int rows) {
        return new IntGrid(cols, rows, Vector.empty());
    }

    public static IntGrid of(int cols, int rows, Integer... values) {
        return new IntGrid(cols, rows, Vector.of(values));
    }

    public static IntGrid of(int cols, int rows, Vector<Integer> values) {
        return new IntGrid(cols, rows, values);
    }

    public static IntGrid filled(int cols, int rows, Integer fillValue) {
        requireNonNull(fillValue, "Fill value must not be null");
        if (fillValue < 0) {
            throw new IllegalArgumentException("Fill value must be non-negative");
        }
        if (cols <= 0 || rows <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        return new IntGrid(cols, rows, Vector.fill(cols * rows, fillValue));
    }

    // =================================================================================================================
    // COVARIANT OVERRIDES
    // =================================================================================================================

    @Override
    public IntGrid set(int col, int row, Integer value) {
        return (IntGrid) super.set(col, row, value);
    }

    @Override
    public IntGrid set(Position position, Integer value) {
        return (IntGrid) super.set(position, value);
    }

    @Override
    public IntGrid updateAll(Function1<Integer, Integer> mapper) {
        return (IntGrid) super.updateAll(mapper);
    }

    // =================================================================================================================
    // TYPE-SPECIFIC METHODS
    // =================================================================================================================

    public int validLocationCount(final Integer validValue) {
        requireNonNull(validValue, "Valid value must not be null");
        return cells.filter(cell -> cell.equals(validValue)).size();
    }

    public int leastPositiveValue() {
        return cells.filter(cell -> cell > 0).min().getOrElse(0);
    }

    public Position randomLocation(final Integer validValue) {
        requireNonNull(validValue, "Valid value must not be null");

        Vector<Integer> validIndices = cells.zipWithIndex()
                .filter(tuple -> tuple._1.equals(validValue))
                .map(tuple -> tuple._2)
                .toVector();

        if (validIndices.isEmpty()) {
            return new Position(-1, -1);
        }

        int randomIndex = random.randomRange(0, validIndices.size() - 1);
        int cellIndex = validIndices.get(randomIndex);
        return new Position(cellIndex % cols, cellIndex / cols);
    }

    public Position randomLeastPositiveLocation(final boolean deterministic) {
        int targetValue = leastPositiveValue();
        if (targetValue == 0) {
            return new Position(-1, -1);
        }

        Vector<Integer> validIndices = cells.zipWithIndex()
                .filter(tuple -> tuple._1.equals(targetValue))
                .map(tuple -> tuple._2)
                .toVector();

        if (validIndices.isEmpty()) {
            return new Position(-1, -1);
        }

        int index = deterministic ? validIndices.size() / 2 : random.randomRange(0, validIndices.size() - 1);
        int cellIndex = validIndices.get(index);
        return new Position(cellIndex % cols, cellIndex / cols);
    }

    // =================================================================================================================
    // EDITOR
    // =================================================================================================================

    public Edit edit() {
        return new Edit(this);
    }

    public static class Edit {

        private final int cols;
        private final int rows;
        private Vector<Integer> cells;

        private Edit(final IntGrid intGrid) {
            this.cols = intGrid.cols;
            this.rows = intGrid.rows;
            this.cells = intGrid.cells;
        }

        private boolean isInBounds(final int col, final int row) {
            return col >= 0 && col < cols && row >= 0 && row < rows;
        }

        public Edit fill(final Integer value) {
            requireNonNull(value, "Fill value is required");

            if (value < 0) {
                throw new IllegalArgumentException("Fill value must be non-negative");
            }

            if (cells == null || cells.isEmpty() || cells.forAll(cell -> cell.equals(value))) {
                return this;
            }

            cells = Vector.fill(cols * rows, value);
            return this;
        }

        public Edit findReplace(final Integer findValueMin, final Integer findValueMax, final Integer fillValue) {
            requireNonNull(findValueMin, "Minimum search value is required");
            requireNonNull(findValueMax, "Maximum search value is required");
            requireNonNull(fillValue, "Fill value is required");

            if (findValueMax < 0 || findValueMin < 0) {
                throw new IllegalArgumentException("Search values must be non-negative");
            }

            if (fillValue < 0) {
                throw new IllegalArgumentException("Fill value must be non-negative");
            }

            if (findValueMin > findValueMax) {
                throw new IllegalArgumentException("Minimum search value cannot be greater than maximum search value");
            }

            if (cells.forAll(cell -> cell < findValueMin || cell > findValueMax)) {
                return this;
            }

            cells = cells.map(cell -> (cell >= findValueMin && cell <= findValueMax) ? fillValue : cell);
            return this;
        }

        public Edit floodFill(final Position position,
                              final Integer eligibleValueMin,
                              final Integer eligibleValueMax,
                              final Integer fillValue) {
            requireNonNull(position, "Position is required");
            return floodFill(position.col(), position.row(), eligibleValueMin, eligibleValueMax, fillValue);
        }

        public Edit floodFill(final int col,
                              final int row,
                              final Integer eligibleValueMin,
                              final Integer eligibleValueMax,
                              final Integer fillValue) {
            requireNonNull(eligibleValueMin, "Minimum eligible value is required");
            requireNonNull(eligibleValueMax, "Maximum eligible value is required");
            requireNonNull(fillValue, "Fill value is required");

            if (eligibleValueMax < 0 || eligibleValueMin < 0) {
                throw new IllegalArgumentException("Eligible values must be non-negative");
            }

            if (fillValue < 0) {
                throw new IllegalArgumentException("Fill value must be non-negative");
            }

            if (eligibleValueMin > eligibleValueMax) {
                throw new IllegalArgumentException("Minimum eligible value cannot be greater than maximum eligible value");
            }

            if (fillValue > eligibleValueMin && fillValue < eligibleValueMax) {
                throw new IllegalArgumentException("Fill value must be outside the eligible value range");
            }

            if (!isInBounds(col, row)) {
                throw new IllegalArgumentException("Starting position is out of bounds");
            }

           this.cells = this.cells.update(row * cols + col, fillValue);

            for (int direction = 0; direction < 4; direction++) {
                int newRow = row + Constants.NB_DIRS[direction][0];
                int newCol = col + Constants.NB_DIRS[direction][1];

                if (isInBounds(newCol, newRow) &&
                    cells.get(newRow * cols + newCol) >= eligibleValueMin &&
                    cells.get(newRow * cols + newCol) <= eligibleValueMax) {
                    floodFill(newCol, newRow, eligibleValueMin, eligibleValueMax, fillValue);
                }
            }
            return this;
        }

        public Edit drawRectangle(final int col,
                                  final int row,
                                  final int width,
                                  final int height,
                                  final Integer fillValue) {
            requireNonNull(fillValue, "Fill value is required");
            if (fillValue < 0) {
                throw new IllegalArgumentException("Fill value must be non-negative");
            }

            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }

            if (!isInBounds(col, row)) {
                throw new IllegalArgumentException("Starting position is out of bounds");
            }

            Vector<Integer> updatedCells = cells.zipWithIndex().map(t -> {
                Integer current = t._1;
                int cellIndex = t._2;
                int cellRow = cellIndex / cols;
                int cellCol = cellIndex % cols;
                if (cellCol >= col && cellCol < col + width &&
                    cellRow >= row && cellRow < row + height) {
                    return fillValue;
                } else {
                    return current;
                }
            });

            if (updatedCells.equals(cells)) return this;

            cells = updatedCells;
            return this;
        }

        public Edit drawCircle(final int centerCol,
                               final int centerRow,
                               final int radius,
                               final Integer fillValue) {
            requireNonNull(fillValue, "Fill value is required");
            if (fillValue < 0) {
                throw new IllegalArgumentException("Fill value must be non-negative");
            }

            if (radius <= 0) {
                throw new IllegalArgumentException("Radius must be positive");
            }

            if (!isInBounds(centerCol, centerRow)) {
                throw new IllegalArgumentException("Center position is out of bounds");
            }

            final int minCol = Math.max(0, centerCol - radius - 1);
            final int maxCol = Math.min(cols - 1, centerCol + radius);
            final int minRow = Math.max(0, centerRow - radius - 1);
            final int maxRow = Math.min(rows - 1, centerRow + radius);
            final int threshold = radius * radius + radius;

            Vector<Integer> updatedCells = cells.zipWithIndex()
                    .map(t -> {
                        Integer current = t._1;
                        int cellIndex = t._2;
                        int cellRow = cellIndex / cols;
                        int cellCol = cellIndex % cols;

                        if (cellCol >= minCol && cellCol <= maxCol && cellRow >= minRow && cellRow <= maxRow) {
                            int dx = cellCol - centerCol;
                            int dy = cellRow - centerRow;
                            if (dx * dx + dy * dy <= threshold) {
                                return fillValue;
                            }
                        }
                        return current;
                    });

            if (updatedCells.equals(cells)) return this;

            cells = updatedCells;
            return this;
        }

        public IntGrid build() {
            return IntGrid.of(cols, rows, cells);
        }
    }
}
