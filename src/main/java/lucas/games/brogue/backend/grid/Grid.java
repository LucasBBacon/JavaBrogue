package lucas.games.brogue.backend.grid;

import io.vavr.Function1;
import io.vavr.collection.Stream;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import lucas.games.brogue.backend.Position;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A generic, immutable 2D grid backed by a Vavr Vector.
 * @param <T> The type of data stored in the grid cells.
 */
public abstract class Grid<T> {

    protected final int cols;
    protected final int rows;
    protected final Vector<T> cells;

    protected Grid(int cols, int rows, Vector<T> cells) {
        if (cells.size() != cols * rows) {
            throw new IllegalArgumentException(
                    "Vector size (" + cells.size() + ") does not match Grid dimensions ("
                            + cols + "x" + rows + "=" + (cols * rows) + ")"
            );
        }
        this.cols = cols;
        this.rows = rows;
        this.cells = cells;
    }

    // =================================================================================================================
    // ABSTRACT HOOK
    // =================================================================================================================

    public int getCols() { return cols; }

    // =================================================================================================================
    // BASIC ACCESSORS
    // =================================================================================================================

    protected abstract Grid<T> newInstance(Vector<T> newCells);
    public int getRows() { return rows; }
    public int getSize() { return cols * rows; }

    public T get(int col, int row) {
        checkBounds(col, row);
        return cells.get(row * cols + col);
    }

    public T get(Position position) {
        return get(position.col(), position.row());
    }

    public Option<T> getSafe(int col, int row) {
        if (isInBounds(col, row)) {
            return Option.of(cells.get(row * cols + col));
        }
        return Option.none();
    }

    public boolean isInBounds(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    public boolean isInBounds(Position position) {
        return isInBounds(position.col(), position.row());
    }

    // =================================================================================================================
    // IMMUTABLE MODIFIERS
    // =================================================================================================================

    public Grid<T> set(int col, int row, T value) {
        checkBounds(col, row);
        return newInstance(cells.update(row * cols + col, value));
    }

    public Grid<T> set(Position position, T value) {
        return set(position.col(), position.row(), value);
    }

    public Grid<T> updateAll(Function1<T, T> mapper) {
        return newInstance(cells.map(mapper));
    }

    public Grid<T> updateIf(Predicate<T> condition, Function1<T, T> mapper) {
        return newInstance(cells.map(cell -> condition.test(cell) ? mapper.apply(cell) : cell));
    }

    // =================================================================================================================
    // ADVANCED QUERIES
    // =================================================================================================================

    public Vector<T> getRow(int row) {
        checkBounds(0, row);
        int start = row * cols;
        return cells.subSequence(start, start + cols);
    }

    public Vector<T> getCols(int col) {
        checkBounds(col, 0);
        return Vector.range(0, rows)
                .map(row -> cells.get(row * cols + col));
    }

    public Vector<T> getNeighbors(int col, int row, int[][] offsets) {
        return Stream.of(offsets)
                .map(offset -> {
                    int newRow = row + offset[0];
                    int newCol = col + offset[1];
                    return isInBounds(newCol, newRow) ? Option.of(get(newCol, newRow)) : Option.<T>none();
                })
                .flatMap(Option::toStream)
                .toVector();
    }

    public Vector<T> getNeighbors(Position position, int[][] offsets) {
        return getNeighbors(position.col(), position.row(), offsets);
    }

    public Vector<T> getNeighbors(int col, int row) {
        int[][] offsets = { {1,0}, {-1,0}, {0,1}, {0,-1} };
        return getNeighbors(col, row, offsets);
    }

    // =================================================================================================================
    // INTERNAL UTILITIES
    // =================================================================================================================

    protected void checkBounds(int col, int row) {
        if (!isInBounds(col, row)) {
            throw new IndexOutOfBoundsException("Grid index out of bounds: (" + col + ", " + row + ")");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(getRow(i).mkString("[", ", ", "]")).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Grid<?> grid = (Grid<?>) o;
        return cols == grid.cols && rows == grid.rows && Objects.equals(cells, grid.cells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cols, rows, cells);
    }
}
