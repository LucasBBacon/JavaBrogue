package lucas.games.brogue.backend.grid.cells;

public record Tile(
        String name,
        char symbol,
        int drawPriority,
        int flags,
        int mechanicsFlags
) {

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }

    public boolean hasFlags(int... flags) {
        for (int flag : flags) {
            if ((this.flags & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMechanic(int mechanicsFlag) {
        return (this.mechanicsFlags & mechanicsFlag) != 0;
    }

    public boolean hasMechanics(int... mechanicsFlags) {
        for (int flag : mechanicsFlags) {
            if ((this.mechanicsFlags & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    // =================================================================================================================
    // TERRAIN FLAGS SETTERS
    // =================================================================================================================

    public Tile withFlags(int newFlags) {
        return new Tile(this.name, this.symbol, this.drawPriority, newFlags, this.mechanicsFlags);
    }

    public Tile withObstructsPassability() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_PASSABILITY); }
    public Tile withObstructsVision() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_VISION); }
    public Tile withObstructsItems() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_ITEMS); }
    public Tile withObstructsSurfaceEffects() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_SURFACE_EFFECTS); }
    public Tile withObstructsGas() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_GAS); }
    public Tile withObstructsDiagonalMovement() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_DIAGONAL_MOVEMENT); }
    public Tile withSpontaneouslyIgnites() { return this.withFlags(this.flags | TerrainFlags.SPONTANEOUSLY_IGNITES); }
    public Tile withAutoDescent() { return this.withFlags(this.flags | TerrainFlags.AUTO_DESCENT); }
    public Tile withLavaInstaDeath() { return this.withFlags(this.flags | TerrainFlags.LAVA_INSTA_DEATH); }
    public Tile withCausesPoison() { return this.withFlags(this.flags | TerrainFlags.CAUSES_POISON); }
    public Tile withIsFlammable() { return this.withFlags(this.flags | TerrainFlags.IS_FLAMMABLE); }
    public Tile withIsFire() { return this.withFlags(this.flags | TerrainFlags.IS_FIRE); }
    public Tile withEntangles() { return this.withFlags(this.flags | TerrainFlags.ENTANGLES); }
    public Tile withIsDeepWater() { return this.withFlags(this.flags | TerrainFlags.IS_DEEP_WATER); }
    public Tile withCausesDamage() { return this.withFlags(this.flags | TerrainFlags.CAUSES_DAMAGE); }
    public Tile withCausesNausea() { return this.withFlags(this.flags | TerrainFlags.CAUSES_NAUSEA); }
    public Tile withCausesParalysis() { return this.withFlags(this.flags | TerrainFlags.CAUSES_PARALYSIS); }
    public Tile withCausesConfusion() { return this.withFlags(this.flags | TerrainFlags.CAUSES_CONFUSION); }
    public Tile withCausesHealing() { return this.withFlags(this.flags | TerrainFlags.CAUSES_HEALING); }
    public Tile withIsDfTrap() { return this.withFlags(this.flags | TerrainFlags.IS_DF_TRAP); }
    public Tile withCausesExplosiveDamage() { return this.withFlags(this.flags | TerrainFlags.CAUSES_EXPLOSIVE_DAMAGE); }
    public Tile withSacred() { return this.withFlags(this.flags | TerrainFlags.SACRED); }
    public Tile withObstructsScent() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_SCENT); }
    public Tile withIsPathingBlocker() { return this.withFlags(this.flags | TerrainFlags.PATHING_BLOCKER); }
    public Tile withDividesLevel() { return this.withFlags(this.flags | TerrainFlags.DIVIDES_LEVEL); }
    public Tile withLakePathingBlocker() { return this.withFlags(this.flags | TerrainFlags.LAKE_PATHING_BLOCKER); }
    public Tile withWaypointBlocker() { return this.withFlags(this.flags | TerrainFlags.WAYPOINT_BLOCKER); }
    public Tile withMovesItems() { return this.withFlags(this.flags | TerrainFlags.MOVES_ITEMS); }
    public Tile withCanBeBridged() { return this.withFlags(this.flags | TerrainFlags.CAN_BE_BRIDGED); }
    public Tile withObstructsEverything() { return this.withFlags(this.flags | TerrainFlags.OBSTRUCTS_EVERYTHING); }
    public Tile withHarmfulTerrain() { return this.withFlags(this.flags | TerrainFlags.HARMFUL_TERRAIN); }
    public Tile withRespirationImmunities() { return this.withFlags(this.flags | TerrainFlags.RESPIRATION_IMMUNITIES); }

    // =================================================================================================================
    // MECHANICS FLAGS SETTERS
    // =================================================================================================================

    public Tile withMechanicsFlags(int newMechanicsFlags) {
        return new Tile(this.name, this.symbol, this.drawPriority, this.flags, newMechanicsFlags);
    }

    public Tile withIsSecret() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.IS_SECRET); }
    public Tile withPromotesWithKey() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_WITH_KEY); }
    public Tile withPromotesWithoutKey() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_WITHOUT_KEY); }
    public Tile withPromotesOnCreature() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_CREATURE); }
    public Tile withPromotesOnItem() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_ITEM); }
    public Tile withPromotesOnItemPickup() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_ITEM_PICKUP); }
    public Tile withPromotesOnPlayerEntry() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_PLAYER_ENTRY); }
    public Tile withPromotesOnSacrificeEntry() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_SACRIFICE_ENTRY); }
    public Tile withPromotesOnElectricity() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_ELECTRICITY); }
    public Tile withAllowsSubmerging() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.ALLOWS_SUBMERGING); }
    public Tile withIsWired() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.IS_WIRED); }
    public Tile withIsCircuitBreaker() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.IS_CIRCUIT_BREAKER); }
    public Tile withGasDissipates() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.GAS_DISSIPATES); }
    public Tile withGasDissipatesQuickly() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.GAS_DISSIPATES_QUICKLY); }
    public Tile withExtinguishesFire() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.EXTINGUISHES_FIRE); }
    public Tile withVanishesUponPromotion() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.VANISHES_UPON_PROMOTION); }
    public Tile withReflectsBolts() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.REFLECTS_BOLTS); }
    public Tile withStandInTile() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.STAND_IN_TILE); }
    public Tile withListInSidebar() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.LIST_IN_SIDEBAR); }
    public Tile withVisuallyDistinct() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.VISUALLY_DISTINCT); }
    public Tile withBrightMemory() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.BRIGHT_MEMORY); }
    public Tile withExplosivePromote() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.EXPLOSIVE_PROMOTE); }
    public Tile withConnectsLevel() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.CONNECTS_LEVEL); }
    public Tile withInterruptExplorationWhenSeen() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.INTERRUPT_EXPLORATION_WHEN_SEEN); }
    public Tile withInvertWhenHighlighted() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.INVERT_WHEN_HIGHLIGHTED); }
    public Tile withSwapEnchantsActivation() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.SWAP_ENCHANTS_ACTIVATION); }
    public Tile withPromotesOnStep() { return this.withMechanicsFlags(this.mechanicsFlags | TerrainMechFlags.PROMOTES_ON_STEP); }

    // =================================================================================================================
    // PREDEFINED TILES
    // =================================================================================================================

    public static final Tile NOTHING = new Tile(
            "Nothing",
            ' ',
            100,
            0,
            0
    );
    public static final Tile GRANITE = new Tile(
            "Granite",
            '#',
            0,
            TerrainFlags.OBSTRUCTS_EVERYTHING,
            TerrainMechFlags.STAND_IN_TILE);
    public static final Tile FLOOR = new Tile(
            "Floor",
            '.',
            95,
            0,
            0
    );
    public static final Tile WALL = new Tile(
            "Wall",
            '#',
            0,
            TerrainFlags.OBSTRUCTS_EVERYTHING,
            TerrainMechFlags.STAND_IN_TILE
    );
}
