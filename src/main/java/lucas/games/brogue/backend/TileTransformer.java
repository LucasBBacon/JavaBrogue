package lucas.games.brogue.backend;

@FunctionalInterface
public interface TileTransformer {
    Tile apply(int x, int y, Tile t);
}
