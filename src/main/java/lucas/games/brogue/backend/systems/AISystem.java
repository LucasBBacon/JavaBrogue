package lucas.games.brogue.backend.systems;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.Tile;
import lucas.games.brogue.backend.entities.Entity;
import lucas.games.brogue.backend.entities.Monster;
import lucas.games.brogue.backend.entities.Player;

import java.util.List;

public class AISystem {

    public void processMonsters(GameManager gameManager) {
        Player player = gameManager.getPlayer();
        List<Entity> entities = gameManager.getEntities();

        // Iterate backward or use a copy to avoid concurrent modification if monsters die
        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                takeTurn((Monster) entity, player, gameManager);
            }
        }
    }

    private void takeTurn(Monster monster, Player player, GameManager gameManager) {
        if (monster.isDead()) return;

        Position monsterPos = monster.getPosition();
        Position playerPos = player.getPosition();

        // 1. Check distance
        double distance = monsterPos.distance(playerPos);
        if (distance > monster.getViewDistance()) return; // too far away, do nothing

        // 2. Check line of sight
        if (!hasLineOfSight(monsterPos, playerPos, gameManager.getDungeonLevel())) return;

        // 3. Combat or move
        if (distance < 1.5) attack(monster, player, gameManager); // adjacent, diagonals are ~1.4
        else moveTowards(monster, playerPos, gameManager); // move closer
    }

    /**
     * Bresenham's Line Algorithm to check for obstacles between two points
     * @return true if there is a clear line of sight, false if blocked.
     */
    private boolean hasLineOfSight(Position start, Position end, DungeonLevel level) {
        int x0 = start.x();
        int y0 = start.y();
        int x1 = end.x();
        int y1 = end.y();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x0 == x1 && y0 == y1) return true; // reached the end

            // Check if this tile blocks light
            // Skip the starting tile (the monster's position)
            if (x0 != start.x() || y0 != start.y()) {
                Tile t = level.getTile(x0, y0);
                if (t == null || t.getTerrain().blocksLight()) {
                    return false;
                }
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private void attack(Monster attacker, Player target, GameManager gm) {
        // Simple combat logic
        int damage = attacker.getDamage();
        target.takeDamage(damage);

        gm.log("The " + attacker.getName() + " hits you for " + damage + " damage!");

        if (target.isDead()) {
            gm.log("You die...");
        }
    }

    private void moveTowards(Monster monster, Position target, GameManager gm) {
        Position current = monster.getPosition();
        Position bestMove = current;
        double closestDist = current.distanceSquared(target);

        // Check all 8 neighbours
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                Position candidate = current.offset(dx, dy);

                // must be passable and closer to target
                if (gm.getDungeonLevel().isValidCoordinate(candidate) &&
                    gm.getDungeonLevel().getTile(candidate).getTerrain().isPassable() &&
                    !gm.getDungeonLevel().getTile(candidate).hasOccupant()) {
                    double dist = candidate.distanceSquared(target);
                    if (dist < closestDist) {
                        closestDist = dist;
                        bestMove = candidate;
                    }
                }
            }
        }

        if (!bestMove.equals(current)) {
            gm.moveEntity(monster, bestMove);
        }
    }
}
