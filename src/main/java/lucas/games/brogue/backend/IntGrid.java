package lucas.games.brogue.backend;

import io.vavr.collection.Vector;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class IntGrid implements Grid<Integer> {

    private final int cols;
    private final int rows;
    private final Vector<Integer> cells;
    private final Random random = new Random();

    private IntGrid(final Builder builder) {
        this.cols = builder.cols;
        this.rows = builder.rows;
        this.cells = builder.cells;
    }

    public int validLocationCount(Integer validValue) {
        requireNonNull(validValue, "Valid value must not be null");
        return (int) cells.filter(cell -> cell.equals(validValue)).size();
    }

    public int leastPositiveValue() {
        return cells.filter(cell -> cell > 0).min().getOrElse(0);
    }

    public Position randomLocation(Integer validValue) {
        requireNonNull(validValue, "Valid value must not be null");
        int row = -1;
        int col = -1;

        int locationCount = validLocationCount(validValue);
        if (locationCount <= 0) {
            return new Position(col, row);
        }

        int index = random.randomRange(0, locationCount - 1);
        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).equals(validValue)) {
                if (index == 0) {
                    row = i / cols;
                    col = i % cols;
                }
                index--;
            }
        }
        return new Position(col, row);
    }

    public Position randomLeastPositiveLocation(boolean deterministic) {
        int targetValue = leastPositiveValue();
        if (targetValue == 0) {
            return new Position(-1, -1);
        }

        int locationCount = 0;
        for (Integer cell : cells) {
            if (cell.equals(targetValue)) {
                locationCount++;
            }
        }

        int index;
        if (deterministic) {
            index = locationCount / 2;
        } else {
            index = random.randomRange(0, locationCount - 1);
        }

        for (int i = 0; i < cells.size(); i++) {
            if (cells.get(i).equals(targetValue)) {
                if (index == 0) {
                    int row = i / cols;
                    int col = i % cols;
                    return new Position(col, row);
                }
                index--;
            }
        }
        // Should not be reachable, since we should already have hit the unique index == 0 point
        return new Position(-1, -1);
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public boolean isInBounds(final int col, final int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    @Override
    public boolean isInBounds(final Position position) {
        return isInBounds(position.col(), position.row());
    }

    @Override
    public Integer get(int col, int row) {
        return cells.get(row * cols + col);
    }

    @Override
    public Integer get(Position position) {
        return Grid.super.get(position);
    }

    @Override
    public Grid<Integer> copy() {
        return new IntGrid.Builder()
                .withDimensions(cols, rows)
                .withCells(cells)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntGrid intGrid = (IntGrid) o;
        return cols == intGrid.cols && rows == intGrid.rows && Objects.equals(cells, intGrid.cells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cols, rows, cells);
    }

    @Override
    public String toString() {
        return cells
                .grouped(cols)
                .map(rowCells -> rowCells.mkString(" "))
                .mkString(System.lineSeparator());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int cols = 10;
        private int rows = 20;
        private Vector<Integer> cells;

        public Builder withDimensions(final int cols, final int rows) {
            if (cols < 0 || rows < 0) {
                throw new IllegalArgumentException("cols and rows must be non-negative");
            }

            this.cols = cols;
            this.rows = rows;
            this.cells = Vector.fill(cols * rows, 0);
            return this;
        }

        public Builder withCells(final Vector<Integer> cells) {
            requireNonNull(cells, "cells must not be null");

            if (cells.size() != cols * rows) {
                throw new IllegalArgumentException("cells size must be equal to cols * rows");
            }

            this.cells = Vector.ofAll(cells);
            return this;
        }

        public IntGrid build() {
            if (cells == null || cells.isEmpty()) {
                this.cells = Vector.fill(cols * rows, 0);
            }
            return new IntGrid(this);
        }
    }

    public Edit edit() {
        return new Edit(this);
    }

    public static class Edit {

        private final int cols;
        private final int rows;
        private Vector<Integer> cells;

        private static final Integer[][] NB_DIRS = {
                {0,-1}, {0,1}, {-1,0}, {1,0}, {-1,-1}, {-1,1}, {1,-1}, {1,1}
        };

        private Edit(final IntGrid intGrid) {
            this.cols = intGrid.cols;
            this.rows = intGrid.rows;
            this.cells = intGrid.cells;
        }

        private boolean isInBounds(final int col, final int row) {
            return col >= 0 && col < cols && row >= 0 && row < rows;
        }

        private void setCell(final int col, final int row, final Integer value) {
            cells = cells.update(row * cols + col, value);
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

            setCell(col, row, fillValue);
            for (int direction = 0; direction < 4; direction++) {
                int newRow = row + NB_DIRS[direction][0];
                int newCol = col + NB_DIRS[direction][1];

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

            Vector<Integer> updatedCells = cells.zipWithIndex().map(t -> {
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
            return new IntGrid.Builder()
                    .withDimensions(cols, rows)
                    .withCells(cells)
                    .build();
        }
    }
}
