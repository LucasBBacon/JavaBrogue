package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;

import java.util.List;

/**
 * The user-controlled character.
 * Represented by the '@' symbol in the game.
 */
public class Player extends Creature {

    private final Inventory inventory;
    private int damage;

    public Player(Position startPosition) {
        super(
                startPosition,
                '@',
                BrogueColor.WHITE,
                "You",
                100 // Standard Brogue starting HP
        );
        this.inventory = new Inventory();
        this.damage = 10; // Standard Brogue starting damage
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
