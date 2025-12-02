package lucas.games.brogue.backend;

public record Tile(TileType type) {

    public Tile() {
        this(TileType.EMPTY);
    }

    public boolean isEmpty() {
        return true;
    }

    public enum TileType {
        EMPTY,
        WALL,
        WATER
    }
}
