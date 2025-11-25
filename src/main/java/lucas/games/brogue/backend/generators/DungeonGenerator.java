package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.*;

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
     * Generates a basic dungeon layout.
     * Clears the level and places connected rooms.
     */
    public void generate() {
        // 1. Fill with walls (implied by new level, but will be safer this way)
        // fillWalls();

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
            }
        }
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
}
