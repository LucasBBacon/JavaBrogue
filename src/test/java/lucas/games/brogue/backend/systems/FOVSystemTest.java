package lucas.games.brogue.backend.systems;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FOVSystemTest {

    @Test
    void testVisionBlockedByWalls() {
        // Setup a 5x5 grid
        DungeonLevel level = new DungeonLevel(5, 5);
        FOVSystem fov = new FOVSystem();

        // Layout:
        // @ # .
        // Player at (0,0), Wall at (1,0), Floor at (2,0)
        // Player should NOT see (2,0) because the wall blocks it

        level.getTile(0, 0).setTerrain(TerrainType.FLOOR);
        level.getTile(1, 0).setTerrain(TerrainType.WALL);
        level.getTile(2, 0).setTerrain(TerrainType.FLOOR);

        Position start = new Position(0, 0);

        fov.calculateFOV(level, start, 5);

        // Origin should be visible
        assertTrue(level.getTile(0, 0).isVisible(), "Player should see themselves");

        // Wall should be visible (you can see the face of the wall)
        assertTrue(level.getTile(1, 0).isVisible(), "Player should see the wall");

        // Floor behind wall should NOT be visible
        assertFalse(level.getTile(2, 0).isVisible(), "Wall should block vision");
        assertFalse(level.getTile(2, 0).isExplored(), "Hidden area should not be explored");
    }

    @Test
    void testOpenVision() {
        DungeonLevel level = new DungeonLevel(10, 10);
        // All floors
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                level.getTile(x, y).setTerrain(TerrainType.FLOOR);

        FOVSystem fov = new FOVSystem();
        Position center = new Position(5, 5);

        fov.calculateFOV(level, center, 3);

        // A tile nearby should be visible
        assertTrue(level.getTile(6, 5).isVisible(), "Nearby tile should be visible");

        // A tile far away should not be visible
        assertFalse(level.getTile(0, 0).isVisible(), "Distant tile should not be visible");
    }
}