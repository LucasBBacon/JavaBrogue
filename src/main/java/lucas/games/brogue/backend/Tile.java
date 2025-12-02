package lucas.games.brogue.backend;

public record Tile(TileType type, TileDisplayBuffer buffer, int terrainFlags) {

    public boolean hasTerrainFlag(int flag)           { return (terrainFlags & flag) != 0; }
    public boolean obstructsPassability()      { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_PASSABILITY); }
    public boolean obstructsVision()           { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_VISION); }
    public boolean obstructsItems()            { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_ITEMS); }
    public boolean obstructsSurfaceEffects()   { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_SURFACE_EFFECTS); }
    public boolean obstructsGas()              { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_GAS); }
    public boolean obstructsDiagonalMovement() { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_DIAGONAL_MOVEMENT); }
    public boolean spontaneouslyIgnites()      { return hasTerrainFlag(TerrainFlags.SPONTANEOUSLY_IGNITES); }
    public boolean autoDescent()               { return hasTerrainFlag(TerrainFlags.AUTO_DESCENT); }
    public boolean lavaInstaDeath()            { return hasTerrainFlag(TerrainFlags.LAVA_INSTA_DEATH); }
    public boolean causesPoison()              { return hasTerrainFlag(TerrainFlags.CAUSES_POISON); }
    public boolean isFlammable()               { return hasTerrainFlag(TerrainFlags.IS_FLAMMABLE); }
    public boolean isFire()                    { return hasTerrainFlag(TerrainFlags.IS_FIRE); }
    public boolean entangles()                 { return hasTerrainFlag(TerrainFlags.ENTANGLES); }
    public boolean isDeepWater()               { return hasTerrainFlag(TerrainFlags.IS_DEEP_WATER); }
    public boolean causesDamage()              { return hasTerrainFlag(TerrainFlags.CAUSES_DAMAGE); }
    public boolean causesNausea()              { return hasTerrainFlag(TerrainFlags.CAUSES_NAUSEA); }
    public boolean causesParalysis()           { return hasTerrainFlag(TerrainFlags.CAUSES_PARALYSIS); }
    public boolean causesConfusion()           { return hasTerrainFlag(TerrainFlags.CAUSES_CONFUSION); }
    public boolean causesHealing()             { return hasTerrainFlag(TerrainFlags.CAUSES_HEALING); }
    public boolean isDfTrap()                  { return hasTerrainFlag(TerrainFlags.IS_DF_TRAP); }
    public boolean causesExplosiveDamage()     { return hasTerrainFlag(TerrainFlags.CAUSES_EXPLOSIVE_DAMAGE); }
    public boolean sacredGround()              { return hasTerrainFlag(TerrainFlags.SACRED_GROUND); }
    public boolean obstructsScent()            { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_SCENT); }
    public boolean pathingBlocker()            { return hasTerrainFlag(TerrainFlags.PATHING_BLOCKER); }
    public boolean dividesLevel()              { return hasTerrainFlag(TerrainFlags.DIVIDES_LEVEL); }
    public boolean lakePathingBlocker()        { return hasTerrainFlag(TerrainFlags.LAKE_PATHING_BLOCKER); }
    public boolean waypointBlocker()           { return hasTerrainFlag(TerrainFlags.WAYPOINT_BLOCKER); }
    public boolean movesItems()                { return hasTerrainFlag(TerrainFlags.MOVES_ITEMS); }
    public boolean canBeBridged()              { return hasTerrainFlag(TerrainFlags.CAN_BE_BRIDGED); }
    public boolean obstructsEverything()       { return hasTerrainFlag(TerrainFlags.OBSTRUCTS_EVERYTHING); }
    public boolean harmfulTerrain()            { return hasTerrainFlag(TerrainFlags.HARMFUL_TERRAIN); }
    public boolean respirationImmunities()     { return hasTerrainFlag(TerrainFlags.RESPIRATION_IMMUNITIES); }

    private Tile withTerrainFlag(int flagMask, boolean set) {
        int newFlags;
        if (set) {
            newFlags = this.terrainFlags | flagMask;
        } else {
            newFlags = this.terrainFlags & ~flagMask;
        }
        if (newFlags == this.terrainFlags) return this;
        return new Tile(this.type, this.buffer, newFlags);
    }

    public Tile setFlag(int flag, boolean value)         { return withTerrainFlag(flag, value); }
    public Tile withObstructsPassability(boolean value)  { return withTerrainFlag(TerrainFlags.OBSTRUCTS_PASSABILITY, value); }
    public Tile withObstructsVision(boolean value)       { return withTerrainFlag(TerrainFlags.OBSTRUCTS_VISION, value); }
    public Tile withObstructsItems(boolean value)        { return withTerrainFlag(TerrainFlags.OBSTRUCTS_ITEMS, value); }
    public Tile withObstructsSurfaceEffects(boolean value) { return withTerrainFlag(TerrainFlags.OBSTRUCTS_SURFACE_EFFECTS, value); }
    public Tile withObstructsGas(boolean value)          { return withTerrainFlag(TerrainFlags.OBSTRUCTS_GAS, value); }
    public Tile withObstructsDiagonalMovement(boolean value) { return withTerrainFlag(TerrainFlags.OBSTRUCTS_DIAGONAL_MOVEMENT, value); }
    public Tile withSpontaneouslyIgnites(boolean value)  { return withTerrainFlag(TerrainFlags.SPONTANEOUSLY_IGNITES, value); }
    public Tile withAutoDescent(boolean value)           { return withTerrainFlag(TerrainFlags.AUTO_DESCENT, value); }
    public Tile withLavaInstaDeath(boolean value)        { return withTerrainFlag(TerrainFlags.LAVA_INSTA_DEATH, value); }
    public Tile withCausesPoison(boolean value)          { return withTerrainFlag(TerrainFlags.CAUSES_POISON, value); }
    public Tile withIsFlammable(boolean value)           { return withTerrainFlag(TerrainFlags.IS_FLAMMABLE, value); }
    public Tile withIsFire(boolean value)                { return withTerrainFlag(TerrainFlags.IS_FIRE, value); }
    public Tile withEntangles(boolean value)             { return withTerrainFlag(TerrainFlags.ENTANGLES, value); }
    public Tile withIsDeepWater(boolean value)           { return withTerrainFlag(TerrainFlags.IS_DEEP_WATER, value); }
    public Tile withCausesDamage(boolean value)          { return withTerrainFlag(TerrainFlags.CAUSES_DAMAGE, value); }
    public Tile withCausesNausea(boolean value)          { return withTerrainFlag(TerrainFlags.CAUSES_NAUSEA, value); }
    public Tile withCausesParalysis(boolean value)       { return withTerrainFlag(TerrainFlags.CAUSES_PARALYSIS, value); }
    public Tile withCausesConfusion(boolean value)       { return withTerrainFlag(TerrainFlags.CAUSES_CONFUSION, value); }
    public Tile withCausesHealing(boolean value)         { return withTerrainFlag(TerrainFlags.CAUSES_HEALING, value); }
    public Tile withIsDfTrap(boolean value)              { return withTerrainFlag(TerrainFlags.IS_DF_TRAP, value); }
    public Tile withCausesExplosiveDamage(boolean value) { return withTerrainFlag(TerrainFlags.CAUSES_EXPLOSIVE_DAMAGE, value); }
    public Tile withSacredGround(boolean value)          { return withTerrainFlag(TerrainFlags.SACRED_GROUND, value); }
    public Tile withObstructsScent(boolean value)        { return withTerrainFlag(TerrainFlags.OBSTRUCTS_SCENT, value); }
    public Tile withPathingBlocker(boolean value)        { return withTerrainFlag(TerrainFlags.PATHING_BLOCKER, value); }
    public Tile withDividesLevel(boolean value)          { return withTerrainFlag(TerrainFlags.DIVIDES_LEVEL, value); }
    public Tile withLakePathingBlocker(boolean value)    { return withTerrainFlag(TerrainFlags.LAKE_PATHING_BLOCKER, value); }
    public Tile withWaypointBlocker(boolean value)       { return withTerrainFlag(TerrainFlags.WAYPOINT_BLOCKER, value); }
    public Tile withMovesItems(boolean value)            { return withTerrainFlag(TerrainFlags.MOVES_ITEMS, value); }
    public Tile withCanBeBridged(boolean value)          { return withTerrainFlag(TerrainFlags.CAN_BE_BRIDGED, value); }
    public Tile withObstructsEverything(boolean value)   { return withTerrainFlag(TerrainFlags.OBSTRUCTS_EVERYTHING, value); }
    public Tile withHarmfulTerrain(boolean value)        { return withTerrainFlag(TerrainFlags.HARMFUL_TERRAIN, value); }
    public Tile withRespirationImmunities(boolean value) { return withTerrainFlag(TerrainFlags.RESPIRATION_IMMUNITIES, value); }
}

