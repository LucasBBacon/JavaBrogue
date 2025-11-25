package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {

    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager(20, 20);
    }

    @Test
    void testSpawnPlayer() {
        Position p = new Position(5, 5);

        // Pre-condition: make sure the tile is floor so it can be spawned
        // (Default dungeon is walls, so there must be a spot first for logic to work)
        gameManager.getDungeonLevel().getTile(p).setTerrain(TerrainType.FLOOR);

        gameManager.spawnPlayer(p);

        assertNotNull(gameManager.getPlayer());
        assertEquals(p, gameManager.getPlayer().getPosition());

        // verify Grid linkage
        Tile t = gameManager.getDungeonLevel().getTile(p);
        assertSame(gameManager.getPlayer(), t.getOccupant());
    }

    @Test
    void testMovement() {
        Position start = new Position(1, 1);
        Position end = new Position(1, 2);

        DungeonLevel level = gameManager.getDungeonLevel();

        // Carve small hallway
        level.getTile(start).setTerrain(TerrainType.FLOOR);
        level.getTile(end).setTerrain(TerrainType.FLOOR);

        gameManager.spawnPlayer(start);
        Player player = gameManager.getPlayer();

        // attempt to move
        boolean moved = gameManager.moveEntity(player, end);

        assertTrue(moved);
        assertEquals(end, player.getPosition());
        assertNull(level.getTile(start).getOccupant(), "Old tile should be unoccupied");
        assertSame(player, level.getTile(end).getOccupant(), "New tile should have the player");
    }

    @Test
    void testCollisionWithWall() {
        Position start = new Position(1, 1);
        Position wall = new Position(1, 2);

        DungeonLevel level = gameManager.getDungeonLevel();
        level.getTile(start).setTerrain(TerrainType.FLOOR);
        level.getTile(wall).setTerrain(TerrainType.WALL); // Ensure wall

        gameManager.spawnPlayer(start);

        boolean moved = gameManager.moveEntity(gameManager.getPlayer(), wall);

        assertFalse(moved, "Should not move into a wall");
        assertEquals(start, gameManager.getPlayer().getPosition(), "Player should remain in original position");
    }

    @Test
    void testDungeonGenerationIntegration() {
        // Generate the dungeon
        gameManager.generateDungeon(98765);
        DungeonLevel level = gameManager.getDungeonLevel();

        // Verify that there are floors now
        boolean foundFloor = false;
        Position floorPos = null;

        // Scan the grid to find a generated floor tile
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                if (level.getTile(x, y).getTerrain() == TerrainType.FLOOR) {
                    foundFloor = true;
                    floorPos = new Position(x, y);
                    break;
                }
            }
            if (foundFloor) break;
        }

        assertTrue(foundFloor, "Dungeon generation should create navigable terrain");

        // Verify a player can be spawned on the generated floor
        gameManager.spawnPlayer(floorPos);
        assertNotNull(gameManager.getPlayer());
        assertEquals(floorPos, gameManager.getPlayer().getPosition());
    }
}