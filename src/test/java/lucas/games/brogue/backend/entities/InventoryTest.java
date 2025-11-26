package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import lucas.games.brogue.backend.entities.items.Food;
import lucas.games.brogue.backend.entities.items.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testPickUpItem() {
        GameManager gm = new GameManager(20, 20);
        Position p = new Position(5, 5);

        gm.getDungeonLevel()
                .getTile(p)
                .setTerrain(TerrainType.FLOOR);

        // Spawn food
        Food food = new Food(p);
        gm.spawnEntity(food, p);

        gm.spawnPlayer(p);

        // Assert it is on the ground
        assertTrue(gm.getDungeonLevel()
                .getTile(p)
                .hasItems()
        );
        assertEquals(0, gm.getPlayer().getInventory().size());

        // Pick it up
        boolean pickedUp = gm.pickUpItem();

        assertTrue(pickedUp, "Should successfully pick up item");
        assertEquals(1, gm.getPlayer().getInventory().size());
        assertFalse(gm.getDungeonLevel().getTile(p).hasItems(), "Ground should be empty after pickup");

        // Check contents
        Item itemInBag = gm.getPlayer()
                .getInventory()
                .get(0);
        assertEquals("Ration of Food", itemInBag.getName());
    }

    @Test
    void testInventoryCapacity() {
        GameManager gm = new GameManager(10, 10);
        gm.getDungeonLevel()
                .getTile(0, 0)
                .setTerrain(TerrainType.FLOOR);
        gm.spawnPlayer(new Position(0, 0));

        // Fill inventory (26 items)
        for (int i = 0; i < 26; i++) {
            gm.getPlayer()
                    .getInventory()
                    .add(new Food(new Position(0, 0)));
        }

        assertTrue(gm.getPlayer().getInventory().isFull());

        gm.getDungeonLevel()
                .getTile(1, 0)
                .setTerrain(TerrainType.FLOOR);
        // Try to pick up one more
        Position foodPos = new Position(1, 0);
        Food extra = new Food(foodPos);
        gm.spawnEntity(extra, foodPos);

        gm.moveEntity(gm.getPlayer(), foodPos);
        boolean pickedUp = gm.pickUpItem();
        assertFalse(pickedUp, "Should fail to pick up when inventory is full");
        assertTrue(gm.getDungeonLevel().getTile(foodPos).hasItems(), "Item should remain on ground");
    }
}