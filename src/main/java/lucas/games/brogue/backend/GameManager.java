package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Creature;
import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.Inventory;
import lucas.games.brogue.backend.entities.Player;
import lucas.games.brogue.backend.entities.items.Item;
import lucas.games.brogue.backend.generators.DungeonGenerator;
import lucas.games.brogue.backend.systems.AISystem;
import lucas.games.brogue.backend.systems.FOVSystem;
import lucas.games.brogue.backend.views.MessageLog;

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
    private final AISystem aiSystem;
    private final MessageLog messageLog;
    private Player player;

    public GameManager(int width, int height) {
        this.dungeonLevel = new DungeonLevel(width, height);
        this.entities = new ArrayList<>();
        this.fovSystem = new FOVSystem();
        this.aiSystem = new AISystem();
        this.messageLog = new MessageLog();
    }

    public void log(String message) {
        messageLog.add(message);
    }

    public MessageLog getMessageLog() {
        return messageLog;
    }

    /**
     * Generates a new dungeon using the provided seed.
     * This replaces the current level geometry with a procedurally generated one.
     */
    public void generateDungeon(int seed) {
        DungeonGenerator generator = new DungeonGenerator(dungeonLevel, seed);

        // generate geometry and get teh list of generated items
        List<Entity> generatedLoot = generator.generate();

        // Clear old entities (except player if persisting them, but here we wipe)
        entities.clear();
        player = null; // Player needs to be re-spawned manually usually, or preserved

        // Spawn generated loot
        for (Entity loot : generatedLoot) {
            spawnEntity(loot, loot.getPosition());
        }

        log("Welcome to the Dungeons of Brogue");
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
        if (!tile.getTerrain().isPassable()) return false;

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
            if (entity == player) log("Blocked by wall.");
            return false; // hit a wall
        }

        // occupant check (attack logic could go here)
        // Only check collision if we are moving a "Blocker" (like the Player)
        // TODO: Implement throwing items, items might fly over entities
        if (!(entity instanceof Item) && targetTile.hasOccupant()) {
            Entity occupant = targetTile.getOccupant();

            // Player attacks monster
            if (occupant != entity) {
                if (entity == player && occupant instanceof Creature) {
                    handleCombat(player, (Creature) occupant);
                    processTurn(); // Attacking counts as a turn
                    return true; // bumped into someone
                }
                return false; // Monster bumping into monster
            }
        }

        // Move logic
        if (!(entity instanceof Item)) {
            // Move the occupant reference
            currentTile.setOccupant(null);
            targetTile.setOccupant(entity);
        }

        // update entity internal position
        entity.setPosition(targetPos);

        if (entity == player) {
            updatePlayerFOV();
            processTurn(); // If player moved it counts as a turn
        }

        return true;
    }

    private void handleCombat(Player attacker, Creature target) {
        int damage = attacker.getDamage();
        target.takeDamage(damage);

        log("You hit the " + target.getName() + " for " + damage + " damage.");

        if (target.isDead()) {
            log ("The " + target.getName() + " dies.");

            // Remove from grid
            Tile tile = dungeonLevel.getTile(target.getPosition());
            if (tile.getOccupant() == target) {
                tile.setOccupant(null);
            }

            // Remove from entity list
            entities.remove(target);
        }
    }

    /**
     * The Player attempts to pick up the top item on their current tile.
     * @return true if an item was picked up, false if no item or inventory full.
     */
    public boolean pickUpItem() {
        if (player == null) return false;

        Position pos = player.getPosition();
        Tile tile = dungeonLevel.getTile(pos);

        if (!tile.hasItems()) {
            log("There is nothing to pick up.");
            return false; // Nothing to pick up
        }

        Item item = tile.getTopItem();
        Inventory inv = player.getInventory();

        if (inv.add(item)) {
            // Success - remove from world
            tile.removeItem(item);
            // Technically, it should be removed from the entities list too
            // to stop the engine from tracking it as a map object
            entities.remove(item);
            log("You pick up the " + item.getName() + ".");
            processTurn();
            return true;
        } else {
            log("Your pack is full.");
        }

        return false; // Inventory full or other failure
    }

    /**
     * Uses an item from the player's inventory.
     * @param index The slot index (0-25).
     * @return The result message of the action.
     */
    public String useItem(int index) {
        if (player == null) {
            log("No player found.");
            return "No player found.";
        }

        Inventory inv = player.getInventory();
        Item item = inv.get(index);

        if (item == null) {
            log("No item in that slot.");
            return "No item in that slot.";
        }

        // Execute item logic
        String message = item.use(player);

        // Handle consumption
        if (item.isConsumable()) inv.remove(item);

        log(message);
        return message;
    }

    /**
     * Executes the enemy turn.
     */
    private void processTurn() {
        aiSystem.processMonsters(this);
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
