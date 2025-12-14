package lucas.games.brogue.backend.grid.cells;

public class TerrainMechFlags {

    public static final int IS_SECRET                    = 1;             // successful search or being stepped on while visible transforms it into discoverType
    public static final int PROMOTES_WITH_KEY            = 1 << 1;        // promotes if the key is present on the tile (in your pack, carried by monster, or lying on the ground)
    public static final int PROMOTES_WITHOUT_KEY         = 1 << 2;        // promotes if the key is NOT present on the tile (in your pack, carried by monster, or lying on the ground)
    public static final int PROMOTES_ON_CREATURE         = 1 << 3;        // promotes when a creature or player is on the tile (whether or not levitating)
    public static final int PROMOTES_ON_ITEM             = 1 << 4;        // promotes when an item is on the tile
    public static final int PROMOTES_ON_ITEM_PICKUP      = 1 << 5;        // promotes when an item is lifted from the tile (primarily for altars)
    public static final int PROMOTES_ON_PLAYER_ENTRY     = 1 << 6;        // promotes when the player enters the tile (whether or not levitating)
    public static final int PROMOTES_ON_SACRIFICE_ENTRY  = 1 << 7;        // promotes when the sacrifice target enters the tile (whether or not levitating)
    public static final int PROMOTES_ON_ELECTRICITY      = 1 << 8;        // promotes when hit by a lightning bolt
    public static final int ALLOWS_SUBMERGING            = 1 << 9;        // allows submersible monsters to submerge in this terrain
    public static final int IS_WIRED                     = 1 << 10;       // if wired, promotes when powered, and sends power when promoting
    public static final int IS_CIRCUIT_BREAKER           = 1 << 11;       // prevents power from circulating in its machine
    public static final int GAS_DISSIPATES               = 1 << 12;       // does not just hang in the air forever
    public static final int GAS_DISSIPATES_QUICKLY       = 1 << 13;       // dissipates quickly
    public static final int EXTINGUISHES_FIRE            = 1 << 14;       // extinguishes burning terrain or creatures
    public static final int VANISHES_UPON_PROMOTION      = 1 << 15;       // vanishes when creating promotion dungeon feature, even if the replacement terrain priority doesn't require it
    public static final int REFLECTS_BOLTS               = 1 << 16;       // magic bolts reflect off of its surface randomly (similar to pmap flag IMPREGNABLE)
    public static final int STAND_IN_TILE                = 1 << 17;       // earthbound creatures will be said to stand "in" the tile, not on it
    public static final int LIST_IN_SIDEBAR              = 1 << 18;       // terrain will be listed in the sidebar with a description of the terrain type
    public static final int VISUALLY_DISTINCT            = 1 << 19;       // terrain will be color-adjusted if necessary so the character stands out from the background
    public static final int BRIGHT_MEMORY                = 1 << 20;       // no blue fade when this tile is out of sight
    public static final int EXPLOSIVE_PROMOTE            = 1 << 21;       // when burned, will promote to promoteType instead of burningType if surrounded by tiles with T_IS_FIRE or TM_EXPLOSIVE_PROMOTE
    public static final int CONNECTS_LEVEL               = 1 << 22;       // will be treated as passable for purposes of calculating level connectedness, irrespective of other aspects of this terrain layer
    public static final int INTERRUPT_EXPLORATION_WHEN_SEEN = 1 << 23;    // will generate a message when discovered during exploration to interrupt exploration
    public static final int INVERT_WHEN_HIGHLIGHTED      = 1 << 24;       // will flip fore and back colors when highlighted with pathing
    public static final int SWAP_ENCHANTS_ACTIVATION     = 1 << 25;       // in machine, swap item enchantments when two suitable items are on this terrain, and activate the machine when that happens

    public static final int PROMOTES_ON_STEP             = (PROMOTES_ON_CREATURE | PROMOTES_ON_ITEM);
}
