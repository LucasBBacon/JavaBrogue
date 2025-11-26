package lucas.games.brogue.backend.views;

import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.Tile;

/**
 * A simple text-based renderer to visualise the dungeon state.
 * Useful for debugging logic before a real GUI is attached.
 */
public class ConsoleRenderer {

    /**
     * Converts the current dungeon state into a printable string.
     */
    public String render(DungeonLevel level) {
        StringBuilder sb = new StringBuilder();

        // print top border
        sb.append("+");
        sb.append("-".repeat(Math.max(0, level.getWidth())));
        sb.append("+\n");

        // Iterate Y first (rows), then X (columns)
        for (int y = 0; y < level.getHeight(); y++) {
            sb.append("|"); // Left border
            for (int x = 0; x < level.getWidth(); x++) {
                Tile tile = level.getTile(x, y);
                sb.append(getCharForTile(tile));
            }
            sb.append("|\n");
        }

        // Print bottom border
        sb.append("+");
        sb.append("-".repeat(Math.max(0, level.getWidth())));
        sb.append("+");

        return sb.toString();
    }

    /**
     * Renders the dungeon level with full visibility (for debugging).
     */
    public String renderFullVisibility(DungeonLevel level) {
        StringBuilder sb = new StringBuilder();

        // print top border
        sb.append("+");
        sb.append("-".repeat(Math.max(0, level.getWidth())));
        sb.append("+\n");

        // Iterate Y first (rows), then X (columns)
        for (int y = 0; y < level.getHeight(); y++) {
            sb.append("|"); // Left border
            for (int x = 0; x < level.getWidth(); x++) {
                Tile tile = level.getTile(x, y);
                // Show all terrain and occupants regardless of visibility
                if (tile.hasOccupant()) {
                    sb.append(tile.getOccupant().getSymbol());
                } if (tile.hasItems()) {
                    sb.append(tile.getTopItem().getSymbol());
                } else {
                    sb.append(tile.getTerrain().getSymbol());
                }
            }
            sb.append("|\n");
        }

        // Print bottom border
        sb.append("+");
        sb.append("-".repeat(Math.max(0, level.getWidth())));
        sb.append("+");

        return sb.toString();
    }

    private char getCharForTile(Tile tile) {
        if (tile == null) return ' ';

        // If visible: show actors first, then terrain
        if (tile.isVisible()) {
            // 1. Draw actor if present
            if (tile.hasOccupant()) {
                return tile.getOccupant().getSymbol();
            }
            // 2. Draw items if present
            if (tile.hasItems()) {
                return tile.getTopItem().getSymbol();
            }
            // 3. Draw terrain
            return tile.getTerrain().getSymbol();
        }

        // If explored (memory): show terrain only
        if (tile.isExplored()) {
            // Memory: you remember items where you last saw them
            if (tile.hasItems()) {
                return tile.getTopItem().getSymbol();
            }
            return tile.getTerrain().getSymbol();
        }

        // Unknown: darkness
        return ' ';
    }
}
