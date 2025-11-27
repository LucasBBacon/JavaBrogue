package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.*;
import lucas.games.brogue.backend.data.MonsterTemplate;
import lucas.games.brogue.backend.data.SpawnTable;
import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.Monster;
import lucas.games.brogue.backend.entities.items.Armor;
import lucas.games.brogue.backend.entities.items.Food;
import lucas.games.brogue.backend.entities.items.Gold;
import lucas.games.brogue.backend.entities.items.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a dungeon layout for a given DungeonLevel.
 * Uses procedural generation techniques to create rooms and corridors.
 */
public class DungeonGenerator {

    private final DungeonLevel level;
    private final BrogueRandom random;
    private final int depth;
    private final SpawnTable spawnTable;

    public DungeonGenerator(DungeonLevel level, int seed, int depth) {
        this.level = level;
        this.random = new BrogueRandom(seed);
        this.depth = depth;
        this.spawnTable = initializeSpawnTable();
    }

    // --- Configuration ---
    private SpawnTable initializeSpawnTable() {
        SpawnTable table = new SpawnTable();

        // Define templates
        MonsterTemplate rat    = new MonsterTemplate("Rat", 'r', new BrogueColor(0.5, 0.3, 0.1),
                6, 2, 6);
        MonsterTemplate kobold = new MonsterTemplate("Kobold", 'K', new BrogueColor(0.8, 0.0, 0.0),
                15, 4, 8);
        MonsterTemplate goblin = new MonsterTemplate("Goblin", 'G', new BrogueColor(0.0, 0.8, 0.0),
                25, 6, 9);
        MonsterTemplate ogre   = new MonsterTemplate("Ogre", 'O', new BrogueColor(0.2, 0.6, 0.2),
                50, 12, 10);

        // Define rules: (Template, minDepth, maxDepth, weight)
        // Depth 1-3: Mostly rats, some kobolds
        table.add(rat,    1, 5, 100);
        table.add(kobold, 1, 8, 20);

        // Depth 4+: goblins appear
        table.add(goblin, 4, 100, 30);
        table.add(kobold, 4, 10, 50); // Kobolds become more common

        // Depth 6+: ogres
        table.add(ogre,   6, 100, 10);

        return table;
    }

    /**
     * Generates a basic dungeon layout and returns a list of initial entities (loot).
     * Clears the level and places connected rooms.
     * @return A list of Entities generated during map creation.
     */
    public Position generate(List<Entity> generatedEntities) {
        level.reset();

        List<Rect> rooms = new ArrayList<>();
        int maxRooms = 30;
        int minSize = 6;
        int maxSize = 10;

        for (int i = 0; i < maxRooms; i++) {
            // Generate random dimensions
            int w = random.randomRange(minSize, maxSize);
            int h = random.randomRange(minSize, maxSize);
            // Generate random position (ensure it fits in level with 1 cell padding)
            int x = random.randomRange(1, level.getWidth() - w - 1);
            int y = random.randomRange(1, level.getHeight() - h - 1);

            Rect newRoom = new Rect(x, y, w, h);

            // Check for overlaps
            boolean failed = false;
            for (Rect other : rooms) {
                if (newRoom.intersects(other)) {
                    failed = true;
                    break;
                }
            }

            if (!failed) {
                // Carve the room
                createRoom(newRoom);

                // Connect to previous room (if exists)
                if (!rooms.isEmpty()) {
                    Position newCenter = newRoom.getCenter();
                    Position prevCenter = rooms.get(rooms.size() - 1).getCenter();
                    createCorridor(prevCenter, newCenter);
                }

                rooms.add(newRoom);
                // Attempt to generate loot for this room
                generateRoomContents(newRoom, generatedEntities);
            }
        }

        // --- Stair placement ---
        // Place stairs in the LAST room generated
        if (!rooms.isEmpty()) {
            Rect lastRoom = rooms.getLast();
            Position stairPos = lastRoom.getCenter();
            level.setTile(stairPos.x(), stairPos.y(), new Tile(TerrainType.STAIRS_DOWN));
        }

        // Return the center of the FIRST room for the player start
        if (!rooms.isEmpty()) {
            return rooms.getFirst().getCenter();
        }
        return new Position(1, 1); // Fallback start position
    }

    private void createRoom(Rect room) {
        for (int x = room.x(); x < room.x() + room.width(); x++) {
            for (int y = room.y(); y < room.y() + room.height(); y++) {
                level.setTile(x, y, new Tile(TerrainType.FLOOR));
            }
        }
    }

    /**
     * Creates a corridor between two points.
     * Uses a simple "L" shape (Horizontal then Vertical)
     */
    private void createCorridor(Position start, Position end) {
        int x = start.x();
        int y = start.y();

        // Move horizontally first
        while (x != end.x()) {
            level.setTile(x, y, new Tile(TerrainType.FLOOR));
            x += (end.x() > x) ? 1 : -1;
        }

        // Move vertically
        while (y != end.y()) {
            level.setTile(x, y, new Tile(TerrainType.FLOOR));
            y += (end.y() > y) ? 1 : -1;
        }
    }

    private void generateRoomContents(Rect room, List<Entity> list) {
        Position pos = room.getRandomPosition(random);

        // Difficulty scaling - base 40% chance. Increases by 5% per depth level
        int monsterChance = 40 + (depth * 5);
        if (monsterChance > 90) monsterChance = 90;

        if (random.randomPercent(monsterChance)) {
            MonsterTemplate choice = spawnTable.roll(depth, random); // use the SpawnTable to pick a monster
            if (choice != null) {
                list.add(choice.spawn(pos));
            }
            return; // Don't spawn loot on top of monster
        }

        // Loot scaling - Food becomes rarer deeper down
        if (random.randomPercent(40)) {
            int roll = random.randomInteger(100);

            if (roll < 40) { // 40% food
                list.add(new Food(pos));
            } else if (roll < 70) { // 30% gold
                list.add(new Gold(pos));
            } else if (roll < 85) { // 15% Weapon
                list.add(new Weapon(pos, "Dagger", 4));
            } else { // 15% armor
                list.add(new Armor(pos, "Leather Armor", 2));
            }
        }
    }
}
