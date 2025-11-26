package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.Player;
import lucas.games.brogue.backend.entities.items.Item;
import lucas.games.brogue.backend.generators.DungeonGenerator;
import lucas.games.brogue.backend.systems.FOVSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * The high-level controller that orchestrates the game.
 * It holds the 'Model' (DungeonLevel, Entities) and executes 'Actions'.
 */
public class GameManager {

    private final DungeonLevel dungeonLevel;
    private final List<Entity> entities;
    private final FOVSystem fovSystem;
    private Player player;

    public GameManager(int width, int height) {
        this.dungeonLevel = new DungeonLevel(width, height);
        this.entities = new ArrayList<>();
        this.fovSystem = new FOVSystem();
    }

    /**
     * Generates a new dungeon using the provided seed.
     * This replaces the current level geometry with a procedurally generated one.
     */
    public void generateDungeon(int seed) {
        DungeonGenerator generator = new DungeonGenerator(dungeonLevel, seed);
        generator.generate();
    }

    /**
     * Spawns a player at a specific location.
     * Guaranteed to be the single source of truth for the player instance.
     */
    public void spawnPlayer(Position pos) {
        // Validate position
        if (!dungeonLevel.isValidCoordinate(pos)) {
            throw new IllegalArgumentException("Cannot spawn player out of bounds: " + pos);
        }

        // Create player
        this.player = new Player(pos);

        // Link to the grid
        Tile tile = dungeonLevel.getTile(pos);
        tile.setOccupant(this.player);

        // Track in entity list
        entities.add(this.player);

        updatePlayerFOV();
    }

    /**
     * Spawns a generic entity (monster, item) at a location.
     * @return true if successful, false blocked.
     */
    public boolean spawnEntity(Entity entity, Position pos) {
        if (!dungeonLevel.isValidCoordinate(pos)) return false;

        Tile tile = dungeonLevel.getTile(pos);

        // Basic collision check, don't spawn on walls or occupied tiles
        if (!tile.getTerrain().isPassable() || tile.hasOccupant()) return false;

        // Handle logic based on type
        if (entity instanceof Item) {
            tile.addItem((Item) entity);
        } else {
            // Creature/Blockers cannot spawn on top of each other
            if (tile.hasOccupant()) return false;
            // update grid state
            tile.setOccupant(entity);
        }

        // update entity internal state
        entity.setPosition(pos);
        entities.add(entity);

        return true;
    }

    /**
     * Attempts to move an entity to a new coordinate.
     * Handles collision detection with walls and other entities.
     * @return true if the move was successful, false otherwise.
     */
    public boolean moveEntity(Entity entity, Position targetPos) {
        if (!dungeonLevel.isValidCoordinate(targetPos)) {
            return false;
        }

        Tile currentTile = dungeonLevel.getTile(entity.getPosition());
        Tile targetTile = dungeonLevel.getTile(targetPos);

        // terrain check
        if (!targetTile.getTerrain().isPassable()) {
            return false; // hit a wall
        }

        // occupant check (attack logic could go here)
        // Only check collision if we are moving a "Blocker" (like the Player)
        // TODO: Implement throwing items, items might fly over entities
        if (!(entity instanceof Item)) {
            if (targetTile.hasOccupant()) return false; // bumped into someone

            // Move the occupant reference
            currentTile.setOccupant(null);
            targetTile.setOccupant(entity);
        } else {
            // Moving an item (e.g. throwing/dropping)
            // TODO: Implement item stacking logic if needed
        }

        // update entity internal position
        entity.setPosition(targetPos);

        if (entity == player) {
            updatePlayerFOV();
        }

        return true;
    }

    /**
     * Recalculates the player's field of view.
     */
    private void updatePlayerFOV() {
        if (player != null) {
            // Standard visual radius usually 10 tiles
            fovSystem.calculateFOV(dungeonLevel, player.getPosition(), 10);
        }
    }

    public DungeonLevel getDungeonLevel() { return dungeonLevel; }

    public Player getPlayer() { return player; }

    public List<Entity> getEntities() { return entities; }
}
