package lucas.games.brogue.backend;

/**
 * Defines the static properties of different terrain types.
 * Replaces the C approach of checking flags or ID ranges.
 */
public enum TerrainType {

    //                 Symbol  Passable         BlocksLight     Color
    WALL(       '#',    false, true,  new BrogueColor(0.4, 0.4, 0.4)),
    FLOOR(      '.',    true,  false, new BrogueColor(0.7, 0.7, 0.7)),
    DOOR_CLOSED('+',    false, true,  new BrogueColor(0.6, 0.4, 0.2)),
    DOOR_OPEN(  '/',    true,  false, new BrogueColor(0.6, 0.4, 0.2)),
    CHASM(      ':',    true,  false, new BrogueColor(0.1, 0.1, 0.1)),
    WATER(      '~',    true,  false, new BrogueColor(0.0, 0.2, 0.8)),
    STAIRS_DOWN('>',    true,  false, BrogueColor.MAGENTA);

    private final char symbol;
    private final boolean isPassable;
    private final boolean blocksLight;
    private final BrogueColor color;

    TerrainType(char symbol, boolean isPassable, boolean blocksLight, BrogueColor color) {
        this.symbol = symbol;
        this.isPassable = isPassable;
        this.blocksLight = blocksLight;
        this.color = color;
    }

    public char getSymbol() { return symbol; }
    public boolean isPassable() { return isPassable; }
    public boolean blocksLight() { return blocksLight; }
    public BrogueColor getColor() { return color; }
}
