package lucas.games.brogue.backend.systems;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import lucas.games.brogue.backend.entities.Monster;
import lucas.games.brogue.backend.views.ConsoleRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AITest {

    @Test
    void testMonsterChasesPlayer() {
        GameManager gm = new GameManager(10, 10);
        // Clean floor
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                gm.getDungeonLevel()
                        .getTile(x, y)
                        .setTerrain(TerrainType.FLOOR);

        ConsoleRenderer r = new ConsoleRenderer();

        Position start = new Position(0, 0);
        Position monsterStart = new Position(0, 4);

        gm.spawnPlayer(start);

        Monster kobold = new Monster(monsterStart,
                'K',
                BrogueColor.RED,
                "Kobold",
                20,
                5,
                10
        );
        gm.spawnEntity(kobold, monsterStart);

        // Player waits (moves into wall = skip turn, or add wait action)
        // for test we'll just move player back and forth

        // Turn 1: Player moves nowhere (invalid move), but we need to trigger processRun manually
        // OR move player validly. Move player 0, 0 -> 0, 1.
        gm.moveEntity(gm.getPlayer(), new Position(0, 1));

        // Kobold should have moved from 0, 4 to 0, 3
        assertEquals(new Position(0, 3), kobold.getPosition());
        System.out.println(r.renderFullVisibility(gm.getDungeonLevel()));

        // Turn 2
        gm.moveEntity(gm.getPlayer(), new Position(0, 2));
        // Player is at 0, 2 and Kobold was at 0, 3. Adjacent!
        // If adjacent, attack, do not move
        assertEquals(new Position(0, 3), kobold.getPosition());
        System.out.println(r.renderFullVisibility(gm.getDungeonLevel()));

        // Check damage
        // Kobold dmg is 5, player starts at 100 hp
        assertTrue(gm.getPlayer().getCurrentHp() < 100, "Player should have taken damage from Kobold attack");
    }
}