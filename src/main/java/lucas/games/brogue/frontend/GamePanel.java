package lucas.games.brogue.frontend;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.DungeonLevel;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Tile;
import lucas.games.brogue.backend.views.MessageLog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {

    private final GameManager gameManager;
    private final int tileSize = 16; // pixels per char
    private final Font terminalFont;

    public GamePanel(GameManager gameManager) {
        this.gameManager = gameManager;
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.BLACK);
        // Load a Monospaced font
        this.terminalFont = new Font("Monospaced", Font.BOLD, tileSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Setup graphics
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(terminalFont);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        renderDungeon(g2d);
        renderUI(g2d);
    }

    private void renderDungeon(Graphics2D g) {
        DungeonLevel level = gameManager.getDungeonLevel();
        if (level == null) return;

        int offsetX = 20;
        int offsetY = 40;

        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                Tile tile = level.getTile(x, y);
                if (tile == null) continue;

                // Determine Char and Color
                char symbol = ' ';
                Color color = Color.BLACK;

                if (tile.isVisible()) {
                    if (tile.hasOccupant()) {
                        symbol = tile.getOccupant().getSymbol();
                        // Mapping BrogueColor to AWT Color
                        color = toAwtColor(tile.getOccupant().getColor());
                    } else if (tile.hasItems()) {
                        symbol = tile.getTopItem().getSymbol();
                        color = toAwtColor(tile.getTopItem().getColor());
                    } else {
                        symbol = tile.getTerrain().getSymbol();
                        color = toAwtColor(tile.getTerrain().getColor());
                    }
                } else if (tile.isExplored()) {
                    // Memory color (dim)
                    if (tile.hasItems()) {
                        symbol = tile.getTopItem().getSymbol();
                        color = toAwtColor(tile.getTopItem().getColor().scale(0.5));
                    } else {
                        symbol = tile.getTerrain().getSymbol();
                        color = toAwtColor(tile.getTerrain().getColor().scale(0.3));
                    }
                }

                // Draw
                if (symbol != ' ') {
                    g.setColor(color);
                    g.drawString(String.valueOf(symbol),
                            offsetX + (x * tileSize),
                            offsetY + (y * tileSize)
                    );
                }
            }
        }
    }

    private Color toAwtColor(BrogueColor bc) {
        return new Color(bc.toRgbInt());
    }

    private void renderUI(Graphics2D g) {
        g.setColor(Color.WHITE);
        int bottomY = this.getHeight() - 100;
        int startX = 20;

        // Player stats
        if (gameManager.getPlayer() != null) {
            String stats = String.format("Depth: %d   HP: %d/%d   Damage: %d   Lvl: %d   XP: %d",
                    gameManager.getCurrentDepth(),
                    gameManager.getPlayer().getCurrentHp(),
                    gameManager.getPlayer().getMaxHp(),
                    gameManager.getPlayer().getDamage(),
                    gameManager.getPlayer().getLevel(),
                    gameManager.getPlayer().getExperience()
            );
            g.drawString(stats, startX, bottomY);
        }

        // Message Log
        MessageLog log = gameManager.getMessageLog();
        List<String> messages = log.getRecentMessages(5);
        int msgY = bottomY + 25;

        for (String msg : messages) {
            g.drawString("> " + msg, startX, msgY);
            msgY += 18;
        }
    }
}
