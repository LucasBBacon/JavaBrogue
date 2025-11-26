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
        GameManager gameManager = new GameManager(50, 30);
        gameManager.startNewGame((int) (Math.random() * 10000));

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
