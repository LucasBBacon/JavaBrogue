package lucas.games.brogue.backend.entities.items;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;
import lucas.games.brogue.backend.entities.Creature;
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

    /**
     * Triggers the item's primary effect on the user.
     * @param user The creature using the item.
     * @return A message describing what happened.
     */
    public abstract String use(Creature user);

    /**
     * @return true if the item should be removed from inventory after use.
     */
    public abstract boolean isConsumable();
}
