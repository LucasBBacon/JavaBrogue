package lucas.games.brogue.backend;

public record Position(int col, int row) {

    @Override
    public String toString() {
        return "(" + col + ", " + row + ")";
    }
}
