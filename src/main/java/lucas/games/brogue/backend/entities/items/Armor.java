package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;

public class Armor extends Item {

    private int defense;

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

    public void enchant(int amount) {
        this.defense += amount;
    }

    @Override
    public String use(Creature user, GameManager gameManager) {
        return user.equip(this);
    }

    @Override
    public boolean isConsumable() {
        return false;
    }
}
