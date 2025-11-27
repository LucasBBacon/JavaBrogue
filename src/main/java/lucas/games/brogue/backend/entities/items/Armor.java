package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;

public class Armor extends Item {

    private final int defense;

    public Armor(Position position, String name, int defense) {
        // Armor is typically bracket [
        super(
                position,
                '[',
                new BrogueColor(0.6, 0.6, 0.6),
                name,
                "Protective gear."
        );
        this.defense = defense;
    }

    public int getDefense() { return defense; }

    @Override
    public String use(Creature user) {
        return user.equip(this);
    }

    @Override
    public boolean isConsumable() {
        return false;
    }
}
