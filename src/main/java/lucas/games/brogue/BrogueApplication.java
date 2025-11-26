package lucas.games.brogue;

import lucas.games.brogue.frontend.MainWindow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

public class BrogueApplication {

    public static void main(String[] args) {
        // Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }

}
