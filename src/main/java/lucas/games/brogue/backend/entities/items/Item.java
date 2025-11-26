package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Entity;

/**
 * Base class for all items in the game.
 * Represents pick-up-able objects.
 * Items do not block movement.
 */
public abstract class Item extends Entity {

    private final String name;
    private final String description;

    public Item(Position position,
                char symbol,
                BrogueColor color,
                String name,
                String description) {
        super(position, symbol, color);
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
