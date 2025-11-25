package lucas.games.brogue.backend.systems;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.Tile;

public class FOVSystem {

    // Multipliers for transforming coordinates into 8 octants
    private static final int[][] MULTIPLIERS = {
            {1, 0, 0, -1, -1, 0, 0, 1},
            {0, 1, -1, 0, 0, -1, 1, 0},
            {0, 1, 1, 0, 0, -1, -1, 0},
            {1, 0, 0, 1, -1, 0, 0, -1}
    };

    public void calculateFOV(DungeonLevel level, Position origin, int radius) {
        // Reset visibility for new turn
        level.prepareTurn();

        // Origin is always visible
        Tile originTile = level.getTile(origin);
        if (originTile != null) {
            originTile.setVisible(true);
        }

        // Cast shadows in 8 octants
        for (int i = 0; i < 8; i++) {
            castLight(level, origin, radius, 1, 1.0f, 0.0f,
                      MULTIPLIERS[0][i], MULTIPLIERS[1][i],
                      MULTIPLIERS[2][i], MULTIPLIERS[3][i]);
        }
    }

    private void castLight(DungeonLevel level, Position origin, int radius,
                           int row, float startSlope, float endSlope,
                           int xx, int xy, int yx, int yy) {
        if (startSlope < endSlope) {
            return;
        }

        float nextStartSlope = startSlope;
        for (int i = row; i <= radius; i++) {
            boolean blocked = false;
            for (int dx = -i, dy = -i; dx <= 0; dx++) {
                float lSlope = (dx - 0.5f) / (dy + 0.5f);
                float rSlope = (dx + 0.5f) / (dy - 0.5f);

                if (startSlope < rSlope) {
                    continue;
                } else if (endSlope > lSlope) {
                    break;
                }

                // Transform logic to actual map coordinates
                int mapX = origin.x() + dx * xx + dy * xy;
                int mapY = origin.y() + dx * yx + dy * yy;

                // Radius check (circular vision)
                if ((dx * dx + dy * dy) < (radius * radius)) {
                    Tile tile = level.getTile(mapX, mapY);
                    if (tile != null) {
                        tile.setVisible(true);
                    }
                }

                // Logic for walls blocking light
                Tile tile = level.getTile(mapX, mapY);
                if (blocked) {
                    if (tile == null || tile.getTerrain().blocksLight()) {
                        nextStartSlope = rSlope;
                        continue;
                    } else {
                        blocked = false;
                        startSlope = nextStartSlope;
                    }
                } else if (tile != null && tile.getTerrain().blocksLight() && i < radius) {
                    blocked = true;
                    castLight(level, origin, radius, i + 1, startSlope, lSlope, xx, xy, yx, yy);
                    nextStartSlope = rSlope;
                }
            }
            if (blocked) {
                break;
            }
        }
    }
}
