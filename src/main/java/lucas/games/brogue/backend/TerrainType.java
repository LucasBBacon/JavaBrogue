package lucas.games.brogue.backend;

/**
 * Defines the static properties of different terrain types.
 * Replaces the C approach of checking flags or ID ranges.
 */
public enum TerrainType {

    //                 Symbol  Passable         BlocksLight
    WALL(       '#',    false, true),
    FLOOR(      '.',    true,  false),
    DOOR_CLOSED('+',    false, true),
    DOOR_OPEN(  '/',    true,  false),
    CHASM(      ':',    true,  false),
    WATER(      '~',    true,  false),
    STAIRS_DOWN('>',    true,  false),;

    private final char symbol;
    private final boolean isPassable;
    private final boolean blocksLight;

    TerrainType(char symbol, boolean isPassable, boolean blocksLight) {
        this.symbol = symbol;
        this.isPassable = isPassable;
        this.blocksLight = blocksLight;
    }

    public char getSymbol() { return symbol; }
    public boolean isPassable() { return isPassable; }
    public boolean blocksLight() { return blocksLight; }
}
