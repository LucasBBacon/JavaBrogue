package lucas.games.brogue.backend.grid.cells;

public record WorldCell(
        Tile dungeon,
        Tile liquid,
        Tile surface,
        Tile gas,

        int gasVolume,
        int machineId,
        int flags,

        Tile memoryTerrain,
        int memoryFlags,
        int memoryTerrainFlags,
        int memoryTerrainMechFlags
) {

    // =================================================================================================================
    // WITHERS
    // =================================================================================================================

    public WorldCell withDungeon(Tile newDungeon) {
        return new WorldCell(
                newDungeon,
                this.liquid,
                this.surface,
                this.gas,
                this.gasVolume,
                this.machineId,
                this.flags,
                this.memoryTerrain,
                this.memoryFlags,
                this.memoryTerrainFlags,
                this.memoryTerrainMechFlags
        );
    }

    public WorldCell withLiquid(Tile newLiquid) {
        return new WorldCell(
                this.dungeon,
                newLiquid,
                this.surface,
                this.gas,
                this.gasVolume,
                this.machineId,
                this.flags,
                this.memoryTerrain,
                this.memoryFlags,
                this.memoryTerrainFlags,
                this.memoryTerrainMechFlags
        );
    }

    public WorldCell withSurface(Tile newSurface) {
        return new WorldCell(
                this.dungeon,
                this.liquid,
                newSurface,
                this.gas,
                this.gasVolume,
                this.machineId,
                this.flags,
                this.memoryTerrain,
                this.memoryFlags,
                this.memoryTerrainFlags,
                this.memoryTerrainMechFlags
        );
    }

    public WorldCell withGas(Tile newGas, int newGasVolume) {
        return new WorldCell(
                this.dungeon,
                this.liquid,
                this.surface,
                newGas,
                newGasVolume,
                this.machineId,
                this.flags,
                this.memoryTerrain,
                this.memoryFlags,
                this.memoryTerrainFlags,
                this.memoryTerrainMechFlags
        );
    }

    public WorldCell withGas(Tile newGas) {
        return this.withGas(newGas, this.gasVolume);
    }

    public WorldCell memorize() {
        Tile top = highestPriorityLayer(false);

        // Remember specific flags
        int memoryTerrainFlags = getCombinedTerrainFlags();
        int memoryMechanicFlags = getCombinedTerrainMechanicFlags();

        return new WorldCell(
                this.dungeon, this.liquid, this.surface, this.gas, this.gasVolume, this.machineId, this.flags,
                top, this.flags, memoryTerrainFlags, memoryMechanicFlags
        );
    }

    private Tile highestPriorityLayer(boolean skipGas) {
        int bestPriority = 1000;
        Tile bestTile = this.dungeon;

        Tile[] layers = skipGas
                ? new Tile[]{surface, liquid, dungeon}
                : new Tile[]{gas, surface, liquid, dungeon};

        for (Tile layer : layers) {
            if (layer != null && layer.drawPriority() < bestPriority) {
                bestPriority = layer.drawPriority();
                bestTile = layer;
            }
        }

        return bestTile;
    }

    // =================================================================================================================
    // FLAG CHECKERS
    // =================================================================================================================

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

    public boolean hasMemoryFlag(int memoryFlag) {
        return (memoryFlags & memoryFlag) != 0;
    }

    public boolean hasMemoryFlags(int... memoryFlags) {
        for (int flag : memoryFlags) {
            if ((this.memoryFlags & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMemoryTerrainFlag(int terrainFlag) {
        return (memoryTerrainFlags & terrainFlag) != 0;
    }

    public boolean hasMemoryTerrainFlags(int... terrainFlags) {
        for (int flag : terrainFlags) {
            if ((this.memoryTerrainFlags & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMemoryTerrainMechFlag(int terrainMechFlag) {
        return (memoryTerrainMechFlags & terrainMechFlag) != 0;
    }

    public boolean hasMemoryTerrainMechFlags(int... terrainMechFlags) {
        for (int flag : terrainMechFlags) {
            if ((this.memoryTerrainMechFlags & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    private int getCombinedTerrainFlags() {
        int combined = 0;
        if (dungeon != null) {
            combined |= dungeon.flags();
        }
        if (liquid != null) {
            combined |= liquid.flags();
        }
        if (surface != null) {
            combined |= surface.flags();
        }
        if (gas != null) {
            combined |= gas.flags();
        }
        return combined;
    }

    public boolean tileHasTerrainFlag(int terrainFlag) {
        return (getCombinedTerrainFlags() & terrainFlag) != 0;
    }

    public boolean tileHasTerrainFlags(int... terrainFlags) {
        for (int flag : terrainFlags) {
            if ((getCombinedTerrainFlags() & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean tileHasMechanicFlag(int terrainMechFlag) {
        return (getCombinedTerrainMechanicFlags() & terrainMechFlag) != 0;
    }

    public boolean tileHasMechanicFlags(int... terrainMechFlags) {
        for (int flag : terrainMechFlags) {
            if ((getCombinedTerrainMechanicFlags() & flag) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean cellIsPassableOrDoor() {
        if (!this.hasFlag(TerrainFlags.PATHING_BLOCKER)) {
            return true;
        }
        return this.tileHasMechanicFlags(TerrainMechFlags.IS_SECRET,
                TerrainMechFlags.PROMOTES_WITH_KEY,
                TerrainMechFlags.CONNECTS_LEVEL)
                && this.tileHasTerrainFlag(TerrainFlags.OBSTRUCTS_PASSABILITY);
    }

    public boolean obstructsPassability() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_PASSABILITY) != 0; }
    public boolean obstructsVision() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_VISION) != 0; }
    public boolean obstructsItems() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_ITEMS) != 0; }
    public boolean obstructsSurfaceEffects() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_SURFACE_EFFECTS) != 0; }
    public boolean obstructsGas() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_GAS) != 0; }
    public boolean obstructsDiagonalMovement() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_DIAGONAL_MOVEMENT) != 0; }
    public boolean spontaneouslyIgnites() { return (getCombinedTerrainFlags() & TerrainFlags.SPONTANEOUSLY_IGNITES) != 0; }
    public boolean hasAutoDescent() { return (getCombinedTerrainFlags() & TerrainFlags.AUTO_DESCENT) != 0; }
    public boolean hasLavaInstaDeath() { return (getCombinedTerrainFlags() & TerrainFlags.LAVA_INSTA_DEATH) != 0; }
    public boolean causesPoison() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_POISON) != 0; }
    public boolean isFlammable() { return (getCombinedTerrainFlags() & TerrainFlags.IS_FLAMMABLE) != 0; }
    public boolean isFire() { return (getCombinedTerrainFlags() & TerrainFlags.IS_FIRE) != 0; }
    public boolean entangles() { return (getCombinedTerrainFlags() & TerrainFlags.ENTANGLES) != 0; }
    public boolean isDeepWater() { return (getCombinedTerrainFlags() & TerrainFlags.IS_DEEP_WATER) != 0; }
    public boolean causesDamage() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_DAMAGE) != 0; }
    public boolean causesNausea() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_NAUSEA) != 0; }
    public boolean causesParalysis() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_PARALYSIS) != 0; }
    public boolean causesConfusion() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_CONFUSION) != 0; }
    public boolean causesHealing() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_HEALING) != 0; }
    public boolean isDfTrap() { return (getCombinedTerrainFlags() & TerrainFlags.IS_DF_TRAP) != 0; }
    public boolean causesExplosiveDamage() { return (getCombinedTerrainFlags() & TerrainFlags.CAUSES_EXPLOSIVE_DAMAGE) != 0; }
    public boolean isSacred() { return (getCombinedTerrainFlags() & TerrainFlags.SACRED) != 0; }
    public boolean obstructsScent() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_SCENT) != 0; }
    public boolean isPathingBlocker() { return (getCombinedTerrainFlags() & TerrainFlags.PATHING_BLOCKER) != 0; }
    public boolean dividesLevel() { return (getCombinedTerrainFlags() & TerrainFlags.DIVIDES_LEVEL) != 0; }
    public boolean isLakePathingBlocker() { return (getCombinedTerrainFlags() & TerrainFlags.LAKE_PATHING_BLOCKER) != 0; }
    public boolean isWaypointBlocker() { return (getCombinedTerrainFlags() & TerrainFlags.WAYPOINT_BLOCKER) != 0; }
    public boolean movesItems() { return (getCombinedTerrainFlags() & TerrainFlags.MOVES_ITEMS) != 0; }
    public boolean canBeBridged() { return (getCombinedTerrainFlags() & TerrainFlags.CAN_BE_BRIDGED) != 0; }
    public boolean obstructsEverything() { return (getCombinedTerrainFlags() & TerrainFlags.OBSTRUCTS_EVERYTHING) != 0; }
    public boolean isHarmfulTerrain() { return (getCombinedTerrainFlags() & TerrainFlags.HARMFUL_TERRAIN) != 0; }
    public boolean hasRespirationImmunities() { return (getCombinedTerrainFlags() & TerrainFlags.RESPIRATION_IMMUNITIES) != 0; }

    private int getCombinedTerrainMechanicFlags() {
        int combined = 0;
        if (dungeon != null) {
            combined |= dungeon.mechanicsFlags();
        }
        if (liquid != null) {
            combined |= liquid.mechanicsFlags();
        }
        if (surface != null) {
            combined |= surface.mechanicsFlags();
        }
        if (gas != null) {
            combined |= gas.mechanicsFlags();
        }
        return combined;
    }

    // =================================================================================================================
    // CONSTANTS
    // =================================================================================================================

    public static final WorldCell EMPTY = new WorldCell(
            Tile.WALL, Tile.NOTHING, Tile.NOTHING, Tile.NOTHING,
            0,
            0,
            0,
            Tile.NOTHING, 0, 0, 0
    );
}
