package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;

public class Monster extends Creature {

    private int viewDistance;
    private int damage;

    public Monster(Position position,
                   char symbol,
                   BrogueColor color,
                   String name,
                   int maxHp,
                   int viewDistance,
                   int damage) {
        super(position, symbol, color, name, maxHp);
        this.viewDistance = viewDistance;
        this.damage = damage;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getDamage() {
        return damage;
    }
}
