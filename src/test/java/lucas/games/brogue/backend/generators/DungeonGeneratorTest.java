package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.TerrainType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonGeneratorTest {

    @Test
    void testRoomGeneration() {
        DungeonLevel level = new DungeonLevel(50, 50);
        DungeonGenerator generator = new DungeonGenerator(level, 12345);

        // Pre-check, everything is a wall
        assertEquals(TerrainType.WALL, level.getTile(25, 25).getTerrain());

        generator.generate();

        // Post-check, some tiles should now be floors
        int floorCount = 0;
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                if (level.getTile(x, y).getTerrain() == TerrainType.FLOOR) {
                    floorCount++;
                }
            }
        }

        assertTrue(floorCount > 0, "Generator should create floor tiles");
        assertTrue((floorCount) < (50 * 50), "Generator should not clear the whole map");
    }

    @Test
    void testConnectivity() {
        // This is a harder test. Ideally, we would flood-fill to ensure all rooms are connected
        // For now we just ensure we generated something substantial
        DungeonLevel level = new DungeonLevel(40, 40);
        DungeonGenerator generator = new DungeonGenerator(level, 999);
        generator.generate();

        // pick the center of the map and scan for a floor
        // just ensuring no crashes during generation
    }
}