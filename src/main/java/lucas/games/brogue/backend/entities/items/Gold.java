package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;

public class Gold extends Item {
    public Gold(Position position) {
        super(position,
                '$',
                new BrogueColor(1.0, 0.84, 0.0),
                "Gold",
                "A pile of gold coins.");
    }

    @Override
    public String use(Creature user, GameManager gameManager) {
        return "You cannot use gold directly. Spend it in shops!";
    }

    @Override
    public boolean isConsumable() {
        return false;
    }
}
