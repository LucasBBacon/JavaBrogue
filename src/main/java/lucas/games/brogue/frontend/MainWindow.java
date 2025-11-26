package lucas.games.brogue.frontend;

import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.TerrainType;

import javax.swing.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Brogue: java edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 1. Init backend
        GameManager gameManager = new GameManager(50, 50);
        gameManager.generateDungeon((int) (Math.random() * 100000));

        // Find a valid start spot
        // TODO: smarter spawn logic
        Position startPos = new Position(5, 5);
        // Force floor for safety
        if (gameManager.getDungeonLevel().getTile(startPos).getTerrain() == TerrainType.WALL) {
            gameManager.getDungeonLevel().getTile(startPos).setTerrain(TerrainType.FLOOR);
        }
        gameManager.spawnPlayer(startPos);

        // 2. Init frontend
        GamePanel panel = new GamePanel(gameManager);
        InputHandler input = new InputHandler(gameManager, panel);

        // 3. Wire up
        this.add(panel);
        this.addKeyListener(input);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        panel.repaint();
    }
}
