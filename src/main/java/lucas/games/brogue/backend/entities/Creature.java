package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.items.Item;

/**
 * Represents a living entity with Health, a Name, and mortality.
 */
public class Creature extends Entity {

    private String name;
    private int currentHp;
    private int maxHp;

    // Progression stats
    private int level;
    private int experience;
    private int xpValue; // how much XP this creature gives when killed

    public Creature(Position position,
                    char symbol,
                    BrogueColor color,
                    String name,
                    int maxHp) {
        super(position, symbol, color);
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.level = 1;
        this.experience = 0;
        this.xpValue = 0;
    }

    public String getName()    { return name; }
    public int getCurrentHp()  { return currentHp; }
    public int getMaxHp()      { return maxHp; }
    public int getLevel()      { return level; }
    public int getExperience() { return experience; }
    public int getXpValue()    { return xpValue; }
    public void setXpValue(int xpValue) { this.xpValue = xpValue; }

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

    /**
     * Adds experience and checks for level up.
     * @return true if leveled up, false otherwise.
     */
    public boolean gainExperience(int amount) {
        this.experience += amount;
        // Simple formula: level * 100 XP needed for next level
        int nextLevelThreshold = this.level * 100;

        if (this.experience >= nextLevelThreshold) {
            levelUp();
            return true;
        }

        return false;
    }

    protected void levelUp() {
        this.level++;
        this.maxHp += 10; // Gain 10 HP per level
        this.currentHp = this.maxHp; // Full heal on level up
    }

    // Allow manual setting for initialization or testing
    protected void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    /**
     * Attempts to equip an item.
     * @return Message describing the result.
     */
    public String equip(Item item) {
        return "You cannot equip that.";
    }
}
