package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;

public class Weapon extends Item {

    private int damageBonus;

    public Weapon(Position position, String name, int damageBonus) {
        // Weapons are typically blue-ish in Brogue
        super(
                position,
                ')',
                new BrogueColor(0.5, 0.5, 1.0),
                name,
                "A weapon."
        );
        this.damageBonus = damageBonus;
    }

    public int getDamageBonus() { return damageBonus; }

    public void enchant(int amount) {
        this.damageBonus += amount;
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
