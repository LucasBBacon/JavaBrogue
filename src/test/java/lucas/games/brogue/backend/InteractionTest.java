package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.items.Food;
import lucas.games.brogue.backend.entities.items.Gold;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InteractionTest {

    @Test
    void testEatingFoodHealsPlayer() {
        GameManager gm = new GameManager(20, 20);
        Position p = new Position(5, 5);

        gm.getDungeonLevel()
                .getTile(p)
                .setTerrain(TerrainType.FLOOR);
        gm.spawnPlayer(p);

        // 1. Damage player
        gm.getPlayer().takeDamage(80); // 100 -> 20 HP
        assertEquals(20, gm.getPlayer().getCurrentHp());

        // 2. Give food
        Food food = new Food(p);
        gm.getPlayer().getInventory().add(food);

        // 3. Eat food (slot 0)
        String msg = gm.useItem(0);

        // 4. Verify effects
        assertTrue(msg.contains("feel much better"));
        assertEquals(70, gm.getPlayer().getCurrentHp()); // 20 + 50 = 70
        assertEquals(0, gm.getPlayer().getInventory().size(), "Food should be consumed");
    }

    @Test
    void testGoldIsNotConsumed() {
        GameManager gm = new GameManager(20, 20);
        Position p = new Position(5, 5);
        gm.getDungeonLevel()
                .getTile(p)
                .setTerrain(TerrainType.FLOOR);
        gm.spawnPlayer(p);

        Gold gold = new Gold(p);
        gm.getPlayer().getInventory().add(gold);

        String msg = gm.useItem(0);

        assertFalse(msg.contains("eat"));
        assertEquals(1, gm.getPlayer().getInventory().size(), "Gold should not be consumed");
    }
}
