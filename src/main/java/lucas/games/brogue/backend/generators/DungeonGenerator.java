package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.*;
import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.Monster;
import lucas.games.brogue.backend.entities.items.Food;
import lucas.games.brogue.backend.entities.items.Gold;

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

    public DungeonGenerator(DungeonLevel level, int seed, int depth) {
        this.level = level;
        this.random = new BrogueRandom(seed);
        this.depth = depth;
    }

    /**
     * Generates a basic dungeon layout and returns a list of initial entities (loot).
     * Clears the level and places connected rooms.
     * @return A list of Entities generated during map creation.
     */
    public Position generate(List<Entity> generatedEntities) {
        // 1. Fill with walls (implied by new level, but will be safer this way)
        // fillWalls();
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
            spawnMonster(pos, list);
            return; // Don't spawn loot on top of monster
        }

        // Loot scaling - Food becomes rarer deeper down
        if (random.randomPercent(40)) {
            // 30% chance for Food, otherwise Gold
            if (random.randomPercent(30 - depth)) {
                list.add(new Food(pos));
            } else {
                list.add(new Gold(pos));
            }
        }
    }

    private void spawnMonster(Position pos, List<Entity> list) {
        // Simple difficulty table
        if (depth == 1) {
            // Level 1: mostly rats, rare kobolds
            if (random.randomPercent(80))
                list.add(
                    new Monster(
                        pos,
                        'r',
                        new BrogueColor(0.5, 0.3, 0.1),
                        "Rat",
                        6,
                        2,
                        6
                    )
                );
            else
                list.add(
                        new Monster(
                                pos,
                                'K',
                                new BrogueColor(0.8, 0.0, 0.0),
                                "Kobold",
                                15,
                                4,
                                8
                        )
                );
        } else {
            // Deeper: more kobolds, maybe goblins
            if (random.randomPercent(50))
                list.add(
                    new Monster(
                        pos,
                        'K',
                        new BrogueColor(0.8, 0.0, 0.0),
                        "Kobold",
                        15,
                        4,
                        8
                    )
                );
            else
                list.add(
                    new Monster(
                        pos,
                        'G',
                        new BrogueColor(0.0, 0.8, 0.0),
                        "Goblin",
                        25,
                        6,
                        9
                    )
                );
        }
    }
}
