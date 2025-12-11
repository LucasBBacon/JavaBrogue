package lucas.games.brogue.backend;

public interface Grid<T> {

    int getCols();
    int getRows();

    boolean isInBounds(int col, int row);
    default boolean isInBounds(Position position) {
        return isInBounds(position.col(), position.row());
    }

    T get(int col, int row);
    default T get(Position position) {
        return get(position.col(), position.row());
    }

    Grid<T> copy();
}
