package lucas.games.brogue.backend.views;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import org.junit.jupiter.api.Test;

class ConsoleRendererTest {

    @Test
    void simulateGameLoop() {
        // setup
        int width = 60;
        int height = 20;
        GameManager gm = new GameManager(width, height);
        ConsoleRenderer renderer = new ConsoleRenderer();

        System.out.println("Generating dungeon...");
        gm.generateDungeon(12345);

        // Find a valid spot for the player (cheat by scanning grid)
        DungeonLevel level = gm.getDungeonLevel();
        Position startPos = null;
        for (int x = 10; x < width; x++) {
            for (int y = 5; y < height; y++) {
                if (level.getTile(x, y).getTerrain() == TerrainType.FLOOR) {
                    startPos = new Position(x, y);
                    break;
                }
            }
            if (startPos != null) break;
        }

        if (startPos == null) {
            System.out.println("Could not find floor to spawn player! Bad seed?");
            return;
        }

        gm.spawnPlayer(startPos);
        System.out.println("Player spawned at " + startPos);

        // Render initial state
        System.out.println("=== INITIAL STATE ===");
        System.out.println(renderer.render(level));

        // Move player and render updates
        // Walk east 5 steps
        for (int i = 0; i < 5; i++) {
            Position current = gm.getPlayer().getPosition();
            Position next = current.offset(1, 0);

            boolean moved = gm.moveEntity(gm.getPlayer(), next);

            System.out.println("\n=== TURN " + (i + 1) + " (Moved: " + moved + ") ===");
            System.out.println(renderer.render(level));
        }
    }
}