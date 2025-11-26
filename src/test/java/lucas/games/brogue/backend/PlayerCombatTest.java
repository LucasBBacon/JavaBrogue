package lucas.games.brogue.backend;

import lucas.games.brogue.backend.entities.Monster;
import lucas.games.brogue.backend.views.ConsoleRenderer;
import lucas.games.brogue.backend.views.MessageLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerCombatTest {

    @Test
    void testPlayerKillsMonster() {
        GameManager gm = new GameManager(10, 10);
        ConsoleRenderer renderer = new ConsoleRenderer();
        MessageLog log = gm.getMessageLog();
        gm.getDungeonLevel()
                .getTile(0, 0)
                .setTerrain(TerrainType.FLOOR);
        gm.getDungeonLevel()
                .getTile(0, 1)
                .setTerrain(TerrainType.FLOOR);

        gm.spawnPlayer(new Position(0, 0));

        // Spawn rat
        Monster rat = new Monster(new Position(0, 1),
                'r',
                BrogueColor.RED,
                "Rat",
                6,
                2,
                10);
        gm.spawnEntity(rat, rat.getPosition());

        System.out.println(renderer.render(gm.getDungeonLevel(), log));

        // Player damage is 10, one hit should kill the rat
        gm.moveEntity(gm.getPlayer(), new Position(0, 1));

        System.out.println(renderer.render(gm.getDungeonLevel(), log));

        assertTrue(rat.isDead(), "Rat should be dead after player attack");

        Tile ratTile = gm.getDungeonLevel().getTile(0, 1);
        assertNull(ratTile.getOccupant(), "Dead rat should be removed from the map");

        assertFalse(gm.getEntities().contains(rat), "Dead rat should be removed from the game entities");

        assertTrue(log.getAllMessages().toString().contains("dies"), "Log should record the rat's death");
    }
}