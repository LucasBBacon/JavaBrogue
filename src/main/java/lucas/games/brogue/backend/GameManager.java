package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.*;
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

    private int currentDepth = 1;

    public GameManager(int width, int height) {
        this.dungeonLevel = new DungeonLevel(width, height);
        this.entities = new ArrayList<>();
        this.fovSystem = new FOVSystem();
        this.aiSystem = new AISystem();
        this.messageLog = new MessageLog();
    }

    public void log(String message) { messageLog.add(message); }
    public MessageLog getMessageLog() { return messageLog; }
    public int getCurrentDepth() { return currentDepth; }

    public void startNewGame(int seed) {
        this.currentDepth = 1;
        this.player = null;
        this.entities.clear();
        generateLevel(seed);

        log("Started a new game.");

        // For a new game, we create a fresh player
        // The generator returns the start position
        // But for simplicity in this refactor, we rely on the generato logic to place the player
        // returning the start position
        // Let's assume generateLevel sets up the map, and we spawn player after
    }

    /**
     * Generates a new dungeon using the provided seed.
     * This replaces the current level geometry with a procedurally generated one.
     */
    private void generateLevel(int seed) {
        // Clear entities list but KEEP the player if they exist
        List<Entity> preservedEntities = new ArrayList<>();
        if (this.player != null) {
            preservedEntities.add(this.player);
        }
        this.entities.clear();
        this.entities.addAll(preservedEntities);

        DungeonGenerator generator = new DungeonGenerator(dungeonLevel, seed, currentDepth);

        // Pass entities list to populate
        List<Entity> newEntities = new ArrayList<>();

        Position startPos = generator.generate(newEntities);

        // Spawn generated loot
        for (Entity e : newEntities) {
            spawnEntity(e, e.getPosition());
        }

        if (player == null) {
            spawnPlayer(startPos);
        } else {
            // Move existing player to new start
            Tile t = dungeonLevel.getTile(startPos);
            t.setOccupant(player);
            player.setPosition(startPos);
        }

        log("--- Depth " + currentDepth + " ---");
        updatePlayerFOV();
        System.out.println(entities.toString());
    }

    public void generateDungeon(int seed) {
        generateLevel(seed);
    }

    public boolean descend() {
        Position pos = player.getPosition();

        if (dungeonLevel.getTile(pos).getTerrain() == TerrainType.STAIRS_DOWN) {
            currentDepth++;
            log("You descend deeper into the dungeons...");
            generateLevel((int)(Math.random() * 10000));
            return true;
        } else {
            log("There are no stairs here.");
            return false;
        }
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
        int rawDamage = attacker.getTotalDamage();

        // Defense calculation
        int defense = 0;
        // TODO: In future if monsters wear armor, place here

        int finalDamage = Math.max(1, rawDamage - defense);

        target.takeDamage(finalDamage);
        log("You hit the " + target.getName() + " for " + finalDamage + " damage.");

        if (target.isDead()) {
            log ("The " + target.getName() + " dies.");

            // --- XP Logic ---
            int xp = target.getXpValue();
            log("You gain " + xp + " experience.");

            if (attacker.gainExperience(xp)) {
                log(">>> LEVEL UP! You are now level " + attacker.getLevel() + "!");
                log("Your Max HP and Damage have increased.");
            }

            // Remove from grid
            Tile tile = dungeonLevel.getTile(target.getPosition());
            if (tile.getOccupant() == target) {
                tile.setOccupant(null);
            }

            // Remove from entity list
            entities.remove(target);
        }
    }

    public void handleMonsterAttack(Creature attacker, Player target) {
        int rawDamage = 0;
        if (attacker instanceof Monster) {
            rawDamage = ((Monster) attacker).getDamage();
        }

        int defense = target.getTotalDefense();
        int finalDamage = Math.max(0, rawDamage - defense);

        target.takeDamage(finalDamage);

        if (finalDamage > 0) {
            log("The " + attacker.getName() + " hits you for " + finalDamage + " damage!");
        } else {
            log ("The " + attacker.getName() + " attacks but your armor absorbs the blow!");
        }

        if (target.isDead()) {
            log("You die...");
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
        String message = item.use(player, this);

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
