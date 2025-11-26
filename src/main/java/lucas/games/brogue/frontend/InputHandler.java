package lucas.games.brogue.frontend;

import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Bridges the gap between Java Swing KeyEvents nad the Game Logic
 */
public class InputHandler implements KeyListener {

    private final GameManager gameManager;
    private final GamePanel gamePanel;

    public InputHandler(GameManager gameManager, GamePanel gamePanel) {
        this.gameManager = gameManager;
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameManager.getPlayer() == null) return;

        boolean actionTaken = false;

        // --- Movement ---
        Position next = getPosition(e);
        if (next != null) {
            actionTaken = gameManager.moveEntity(gameManager.getPlayer(), next);
        }

        // --- Actions ---
        if (!actionTaken) {
            switch (e.getKeyCode()) {
                // Pick up item
                case KeyEvent.VK_COMMA:
                case KeyEvent.VK_G:
                    gameManager.pickUpItem();
                    actionTaken = true;
                    break;
                // Handle '>' via Shift+Period usually, but Swing maps keycodes oddly
                case KeyEvent.VK_PERIOD:
                    if (e.isShiftDown()) {
                        gameManager.descend();
                        gamePanel.repaint(); // repaint immediately as map changed
                        return;
                    }
                    break;

                // Handle stairs explicit enter
                case KeyEvent.VK_ENTER:
                    gameManager.descend();
                    gamePanel.repaint();
                    return;

                // Debug - regenerate dungeon
                case KeyEvent.VK_R:
                    gameManager.generateDungeon((int)(Math.random() * 100000));
                    gameManager.spawnPlayer(new Position(5, 5)); // TODO: smarter spawn
                    gamePanel.repaint();
                    return;
            }
        }

        // --- Inventory Usage (1-9) ---
        // '1' is keycode 49
        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
            int slot = e.getKeyCode() - KeyEvent.VK_1;
            gameManager.useItem(slot);
            actionTaken = true;
        }

        // Refresh the screen if an action was taken
        if (actionTaken) {
            gamePanel.repaint();
        }
    }

    private Position getPosition(KeyEvent e) {
        Position current = gameManager.getPlayer().getPosition();
        return switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W, KeyEvent.VK_NUMPAD8 -> current.offset(0, -1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S, KeyEvent.VK_NUMPAD2 -> current.offset(0, 1);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A, KeyEvent.VK_NUMPAD4 -> current.offset(-1, 0);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D, KeyEvent.VK_NUMPAD6 -> current.offset(1, 0);
            // Diagonals (Numpad)
            case KeyEvent.VK_NUMPAD7 -> current.offset(-1, -1);
            case KeyEvent.VK_NUMPAD9 -> current.offset(1, -1);
            case KeyEvent.VK_NUMPAD1 -> current.offset(-1, 1);
            case KeyEvent.VK_NUMPAD3 -> current.offset(1, 1);
            // Wait
            case KeyEvent.VK_NUMPAD5, KeyEvent.VK_PERIOD -> current; // Move to self = wait
            default -> null;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}
