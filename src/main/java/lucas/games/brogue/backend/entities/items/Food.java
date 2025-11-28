package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;

public class Food extends Item {

    public Food(Position position) {
        super(position,
                '%', // Classic Roguelike food symbol
                new BrogueColor(0.8, 0.4, 0.1), // Brownish color
                "Ration of Food",
                "A dry but nutritious standard ration.");
    }

    @Override
    public String use(Creature user, GameManager gameManager) {
        // Heal 50% of max HP
        int healAmount = user.getMaxHp() / 2;
        user.heal(healAmount);

        return "You eat the " + getName() + ". You feel much better.";
    }

    @Override
    public boolean isConsumable() {
        return true;
    }
}
