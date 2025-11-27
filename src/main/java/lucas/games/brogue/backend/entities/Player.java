package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.items.Armor;
import lucas.games.brogue.backend.entities.items.Item;
import lucas.games.brogue.backend.entities.items.Weapon;

/**
 * The user-controlled character.
 * Represented by the '@' symbol in the game.
 */
public class Player extends Creature {

    private final Inventory inventory;
    private int baseDamage;

    // Equipment slots
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Player(Position startPosition) {
        super(
                startPosition,
                '@',
                BrogueColor.WHITE,
                "You",
                100 // Standard Brogue starting HP
        );
        this.inventory = new Inventory();
        this.baseDamage = 5; // Standard Brogue starting damage
    }

    @Override
    protected void levelUp() {
        super.levelUp();
        // Player gains damage on level up
        this.baseDamage += 1;
    }

    @Override
    public String equip(Item item) {
        if (item instanceof Weapon) {
            if (equippedWeapon == item) {
                equippedWeapon = null;
                return "You unequip the " + item.getName() + ".";
            }
            equippedWeapon = (Weapon) item;
            return "You wield the " + item.getName() + ".";
        }

        if (item instanceof Armor) {
            if (equippedArmor == item) {
                equippedArmor = null;
                return "You take off the " + item.getName() + ".";
            }
            equippedArmor = (Armor) item;
            return "You put on the " + item.getName() + ".";
        }
        return "You cannot equip that.";
    }

    public int getTotalDamage() {
        int dmg = baseDamage;
        if (equippedWeapon != null) {
            dmg += equippedWeapon.getDamageBonus();
        }
        return dmg;
    }

    public int getTotalDefense() {
        if (equippedArmor != null) {
            return equippedArmor.getDefense();
        }
        return 0;
    }

    public Inventory getInventory() { return inventory; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public int getBaseDamage() { return baseDamage;}
}
