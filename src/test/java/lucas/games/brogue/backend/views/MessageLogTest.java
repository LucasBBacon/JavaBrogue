package lucas.games.brogue.backend.views;


import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import lucas.games.brogue.backend.entities.Monster;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageLogTest {

    @Test
    void testCombatLog() {
        GameManager gm = new GameManager(10, 10);
        ConsoleRenderer renderer = new ConsoleRenderer();
        gm.getDungeonLevel()
                .getTile(0, 0)
                .setTerrain(TerrainType.FLOOR);
        gm.getDungeonLevel()
                .getTile(1, 0)
                .setTerrain(TerrainType.FLOOR);
        gm.getDungeonLevel()
                .getTile(0, 1)
                .setTerrain(TerrainType.FLOOR);

        gm.spawnPlayer(new Position(1, 0));

        // Spawn Kobold adjacent
        Monster kobold = new Monster(new Position(0, 1),
                'K',
                BrogueColor.RED,
                "Kobold",
                10,
                5,
                10);
        gm.spawnEntity(kobold, new Position(0, 1));

        // Trigger a turn
        // NOTE: In current implementation, invalid move returns false and DOES NOT trigger processTurn()
        // We must perform a valid action or expose processTurn.
        gm.moveEntity(gm.getPlayer(), new Position(0, 0));


        // Check log
        MessageLog log = gm.getMessageLog();
        boolean foundHit = false;

        for (String msg : log.getAllMessages()) {
            if (msg.contains("Kobold hits you")) {
                foundHit = true;
                break;
            }
        }

        assertTrue(foundHit, "Combat log should contain the attack message");

        // Render check
        String output = renderer.render(gm.getDungeonLevel(), log);
        assertTrue(output.contains("Kobold hits you"), "Renderer should display the combat message");
    }
}