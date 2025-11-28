package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.GameManager;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;
import lucas.games.brogue.backend.entities.Player;

public class ScrollEnchanting extends Item {

    public ScrollEnchanting(Position position) {
        super(
                position,
                '?',
                new BrogueColor(0.6, 0.2, 0.8),
                "Scroll of Enchanting",
                "A magical scroll."
        );
    }

    @Override
    public String use(Creature user, GameManager gameManager) {
        if (!(user instanceof Player)) return "Nothing happens.";

        Player player = (Player) user;
        boolean enchanted = false;
        StringBuilder sb = new StringBuilder();

        // Enchant weapon
        if (player.getEquippedWeapon() != null) {
            player.getEquippedWeapon().enchant(1);
            sb.append("Your ")
                    .append(player.getEquippedWeapon().getName())
                    .append(" glows blue! ");
            enchanted = true;
        }

        // Enchant armor
        if (player.getEquippedArmor() != null) {
            player.getEquippedArmor().enchant(1);
            sb.append("Your ")
                    .append(player.getEquippedArmor().getName())
                    .append(" glows silver! ");
            enchanted = true;
        }

        if (!enchanted) {
            return "You shudder, but nothing happens. (Equip something first!)";
        }

        return sb.toString();
    }

    @Override
    public boolean isConsumable() {
        return true;
    }
}
