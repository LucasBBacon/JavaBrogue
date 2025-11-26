package lucas.games.brogue.backend.data;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Monster;

/**
 * A blueprint for creating a specific type of monster.
 * Avoids hardcoding stats inside generator loops.
 */
public record MonsterTemplate(
        String name,
        char symbol,
        BrogueColor color,
        int maxHp,
        int damage,
        int viewDistance
) {
    public Monster spawn(Position pos) {
        return new Monster(pos, symbol, color, name, maxHp, damage, viewDistance);
    }
}
