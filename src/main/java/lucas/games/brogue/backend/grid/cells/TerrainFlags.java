package lucas.games.brogue.backend.grid.cells;

public class TerrainFlags {

    public static final int OBSTRUCTS_PASSABILITY       = 1;             // cannot be walked through
    public static final int OBSTRUCTS_VISION            = 1 << 1;        // blocks line of sight
    public static final int OBSTRUCTS_ITEMS             = 1 << 2;        // items can't be on this tile
    public static final int OBSTRUCTS_SURFACE_EFFECTS   = 1 << 3;        // grass, blood, etc. cannot exist on this tile
    public static final int OBSTRUCTS_GAS               = 1 << 4;        // blocks the permeation of gas
    public static final int OBSTRUCTS_DIAGONAL_MOVEMENT = 1 << 5;        // can't step diagonally around this tile
    public static final int SPONTANEOUSLY_IGNITES       = 1 << 6;        // monsters avoid unless chasing player or immune to fire
    public static final int AUTO_DESCENT                = 1 << 7;        // automatically drops creatures down a depth level and does some damage (2d6)
    public static final int LAVA_INSTA_DEATH            = 1 << 8;        // kills any non-levitating non-fire-immune creature instantly
    public static final int CAUSES_POISON               = 1 << 9;        // any non-levitating creature gets 10 poison
    public static final int IS_FLAMMABLE                = 1 << 10;       // terrain can catch fire
    public static final int IS_FIRE                     = 1 << 11;       // terrain is a type of fire; ignites neighboring flammable cells
    public static final int ENTANGLES                   = 1 << 12;       // entangles players and monsters like a spiderweb
    public static final int IS_DEEP_WATER               = 1 << 13;       // steals items 50% of the time and moves them around randomly
    public static final int CAUSES_DAMAGE               = 1 << 14;       // anything on the tile takes max(1-2, 10%) damage per turn
    public static final int CAUSES_NAUSEA               = 1 << 15;       // any creature on the tile becomes nauseous
    public static final int CAUSES_PARALYSIS            = 1 << 16;       // anything caught on this tile is paralyzed
    public static final int CAUSES_CONFUSION            = 1 << 17;       // causes creatures on this tile to become confused
    public static final int CAUSES_HEALING              = 1 << 18;       // heals 20% max HP per turn for any player or non-inanimate monsters
    public static final int IS_DF_TRAP                  = 1 << 19;       // spews gas of type specified in fireType when stepped on
    public static final int CAUSES_EXPLOSIVE_DAMAGE     = 1 << 20;       // is an explosion; deals higher of 15-20 or 50% damage instantly, but not again for five turns
    public static final int SACRED                      = 1 << 21;       // monsters that aren't allies of the player will avoid stepping here

    public static final int OBSTRUCTS_SCENT             = (OBSTRUCTS_PASSABILITY | OBSTRUCTS_VISION | AUTO_DESCENT | LAVA_INSTA_DEATH | IS_DEEP_WATER | SPONTANEOUSLY_IGNITES);
    public static final int PATHING_BLOCKER             = (OBSTRUCTS_PASSABILITY | AUTO_DESCENT | IS_DF_TRAP | LAVA_INSTA_DEATH | IS_DEEP_WATER | IS_FIRE | SPONTANEOUSLY_IGNITES);
    public static final int DIVIDES_LEVEL               = (OBSTRUCTS_PASSABILITY | AUTO_DESCENT | IS_DF_TRAP | LAVA_INSTA_DEATH | IS_DEEP_WATER);
    public static final int LAKE_PATHING_BLOCKER        = (AUTO_DESCENT | LAVA_INSTA_DEATH | IS_DEEP_WATER | SPONTANEOUSLY_IGNITES);
    public static final int WAYPOINT_BLOCKER            = (OBSTRUCTS_PASSABILITY | AUTO_DESCENT | IS_DF_TRAP | LAVA_INSTA_DEATH | IS_DEEP_WATER | SPONTANEOUSLY_IGNITES);
    public static final int MOVES_ITEMS                 = (IS_DEEP_WATER | LAVA_INSTA_DEATH);
    public static final int CAN_BE_BRIDGED              = (AUTO_DESCENT);
    public static final int OBSTRUCTS_EVERYTHING        = (OBSTRUCTS_PASSABILITY | OBSTRUCTS_VISION | OBSTRUCTS_ITEMS | OBSTRUCTS_GAS | OBSTRUCTS_SURFACE_EFFECTS | OBSTRUCTS_DIAGONAL_MOVEMENT);
    public static final int HARMFUL_TERRAIN             = (CAUSES_POISON | IS_FIRE | CAUSES_DAMAGE | CAUSES_PARALYSIS | CAUSES_CONFUSION | CAUSES_EXPLOSIVE_DAMAGE);
    public static final int RESPIRATION_IMMUNITIES      = (CAUSES_DAMAGE | CAUSES_CONFUSION | CAUSES_PARALYSIS | CAUSES_NAUSEA);
}
