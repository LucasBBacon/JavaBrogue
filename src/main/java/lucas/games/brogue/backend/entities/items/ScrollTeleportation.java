package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.*;
import lucas.games.brogue.backend.entities.Creature;

public class ScrollTeleportation extends Item {

    public ScrollTeleportation(Position position) {
        super(
                position,
                '?',
                new BrogueColor(0.8, 0.8, 1.0),
                "Scroll of Teleportation",
                "A magical scroll."
        );
    }

    @Override
    public String use(Creature user, GameManager gameManager) {
        // 1. Find a random safe spot
        DungeonLevel level = gameManager.getDungeonLevel();
        Position newPos = null;
        int attempts = 0;

        while (attempts < 100) {
            int x = (int)(Math.random() * level.getWidth());
            int y = (int)(Math.random() * level.getHeight());

            // Safe?
            if (level.getTile(x, y).getTerrain() == TerrainType.FLOOR &&
                    !level.getTile(x, y).hasOccupant()) {
                newPos = new Position(x, y);
                break;
            }
            attempts++;
        }

        if (newPos != null) {
            // Teleport logic
            gameManager.moveEntity(user, newPos);
            // Re-calculate field of view
            return "You disappear in a puff of smoke and reappear elsewhere!";
        } else {
            return "The scroll fizzles. Nowhere safe to go!";
        }
    }

    @Override
    public boolean isConsumable() {
        return true;
    }
}
