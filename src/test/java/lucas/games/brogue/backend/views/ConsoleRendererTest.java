package lucas.games.brogue.backend.views;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;
import lucas.games.brogue.backend.entities.items.Food;
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
        Position startPos = findFloor(level);
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

    @Test
    void testRenderFullVisibility() {
        // setup
        int width = 60;
        int height = 20;
        GameManager gm = new GameManager(width, height);
        ConsoleRenderer renderer = new ConsoleRenderer();

        System.out.println("Generating dungeon...");
        gm.generateDungeon(12345);

        // Find a valid spot for the player (cheat by scanning grid)
        DungeonLevel level = gm.getDungeonLevel();
        Position startPos = findFloor(level);
        if (startPos == null) {
            System.out.println("Could not find floor to spawn player! Bad seed?");
            return;
        }

        gm.spawnPlayer(startPos);
        System.out.println("Player spawned at " + startPos);

        // Render initial state
        System.out.println("=== FULL MAP ===");
        System.out.println(renderer.renderFullVisibility(level));
    }

    @Test
    void simulateGameLoopWithItems() {
        int width = 60;
        int height = 20;
        GameManager gm = new GameManager(width, height);
        ConsoleRenderer renderer = new ConsoleRenderer();

        System.out.println("Generating dungeon...");
        gm.generateDungeon(12345);

        // Find a valid spot for the player (cheat by scanning grid)
        DungeonLevel level = gm.getDungeonLevel();
        Position startPos = findFloor(level);
        if (startPos == null) {
            System.out.println("Could not find floor to spawn player! Bad seed?");
            return;
        }

        gm.spawnPlayer(startPos);

        // Spawn food next to player
        Position foodPos = startPos.offset(2, 0);
        // Ensure foodPos is a floor
        level.getTile(foodPos).setTerrain(TerrainType.FLOOR);

        Food food = new Food(foodPos);
        gm.spawnEntity(food, foodPos);

        System.out.println("Spawned food at " + foodPos);

        // Walk towards food
        for (int i = 0; i < 4; i++) {
            Position current = gm.getPlayer().getPosition();
            Position next = current.offset(1, 0);

            boolean moved = gm.moveEntity(gm.getPlayer(), next);

            System.out.println("\n=== TURN " + (i + 1) + " (Moved: " + moved + ") ===");
            System.out.println(renderer.render(level));

            if (gm.getPlayer().getPosition().equals(foodPos)) {
                System.out.println(">>> YUM! Player is standing on the food :P <<<");
            }
        }
    }

    private Position findFloor(DungeonLevel level) {
        for (int x = 10; x < level.getWidth(); x++) {
            for (int y = 5; y < level.getHeight(); y++) {
                if (level.getTile(x, y).getTerrain() == TerrainType.FLOOR) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }
}