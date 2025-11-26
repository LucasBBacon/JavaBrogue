package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.*;
import lucas.games.brogue.backend.entities.Entity;
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

    public DungeonGenerator(DungeonLevel level, int seed) {
        this.level = level;
        this.random = new BrogueRandom(seed);
    }

    /**
     * Generates a basic dungeon layout and returns a list of initial entities (loot).
     * Clears the level and places connected rooms.
     * @return A list of Entities generated during map creation.
     */
    public List<Entity> generate() {
        // 1. Fill with walls (implied by new level, but will be safer this way)
        // fillWalls();
        // TODO: clear grid logic

        List<Rect> rooms = new ArrayList<>();
        List<Entity> generatedLoot = new ArrayList<>();

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
                generateRoomLoot(newRoom, generatedLoot);
            }
        }

        return generatedLoot;
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

    private void generateRoomLoot(Rect room, List<Entity> lootList) {
        // 50% chance for a room to have loot
        if (random.randomPercent(50)) {
            Position pos = room.getRandomPosition(random);

            // 30% chance for Food, otherwise Gold
            if (random.randomPercent(30)) {
                lootList.add(new Food(pos));
            } else {
                lootList.add(new Gold(pos));
            }
        }
    }
}
