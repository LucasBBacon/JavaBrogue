package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;

public class Food extends Item {

    public Food(Position position) {
        super(position,
                '%', // Classic Roguelike food symbol
                new BrogueColor(0.8, 0.4, 0.1), // Brownish color
                "Ration of Food",
                "A dry but nutritious standard ration.");
    }
}
