package lucas.games.brogue.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DungeonLevelTest {

    @Test
    void testInitialization() {
        int w = 20;
        int h = 10;
        DungeonLevel level = new DungeonLevel(w, h);

        assertEquals(w, level.getWidth());
        assertEquals(h, level.getHeight());

        // Check that it's filled with walls
        Tile t = level.getTile(5, 5);
        assertNotNull(t);
        assertEquals(TerrainType.WALL, t.getTerrain());
    }

    @Test
    void testOutOfBoundsAccess() {
        DungeonLevel level = new DungeonLevel(10, 10);

        assertNull(level.getTile(-1, 0), "Negative X should return null");
        assertNull(level.getTile(0, -1), "Negative Y should return null");
        assertNull(level.getTile(10, 5), "Out of bounds X should return null");
        assertNull(level.getTile(5, 10), "Out of bounds Y should return null");
    }

    @Test
    void testSetTile() {
        DungeonLevel level = new DungeonLevel(10, 10);

        Tile floorTile = new Tile(TerrainType.FLOOR);
        level.setTile(5, 5, floorTile);

        assertSame(floorTile, level.getTile(5, 5));
    }
}