package lucas.games.brogue.backend.generators;

import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.items.Food;
import lucas.games.brogue.backend.entities.items.Gold;
import lucas.games.brogue.backend.entities.items.Item;
import lucas.games.brogue.backend.views.ConsoleRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LootGenerationTest {

    @Test
    void testLootIsGenerated() {
        GameManager gm = new GameManager(50, 50);
        gm.generateDungeon(555);
        ConsoleRenderer r = new ConsoleRenderer();

        int itemCount = 0;
        int goldCount = 0;
        int foodCount = 0;

        for (Entity e : gm.getEntities()) {
            if (e instanceof Item) {
                itemCount++;
                if (e instanceof Gold) goldCount++;
                if (e instanceof Food) foodCount++;
            }
        }


        assertTrue(itemCount > 0, "Dungeon should contain generated loot");
        System.out.println("Generated " + itemCount + " items: " + goldCount + " gold, " + foodCount + " food.");

        System.out.println(r.renderFullVisibility(gm.getDungeonLevel()));
    }
}
