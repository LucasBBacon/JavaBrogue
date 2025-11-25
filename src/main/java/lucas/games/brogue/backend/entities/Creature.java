package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;

/**
 * Represents a living entity with Health, a Name, and mortality.
 */
public class Creature extends Entity {

    private String name;
    private int currentHp;
    private int maxHp;

    public Creature(Position position,
                    char symbol,
                    BrogueColor color,
                    String name,
                    int maxHp) {
        super(position, symbol, color);
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void takeDamage(int amount) {
        this.currentHp -= amount;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
    }


    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > maxHp) {
            this.currentHp = maxHp;
        }
    }
}
