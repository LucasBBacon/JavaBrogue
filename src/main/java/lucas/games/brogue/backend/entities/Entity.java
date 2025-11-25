package lucas.games.brogue.backend.entities;

import lucas.games.brogue.backend.BrogueColor;
import lucas.games.brogue.backend.Position;

/**
 * Represents any object that occupies a coordinate in the dungeon.
 * This includes Creatures (Player, Monsters) and Items (on the floor).
 */
public abstract class Entity {

    private Position position;
    private char symbol;
    private BrogueColor color;

    public Entity(Position position, char symbol, BrogueColor color) {
        this.position = position;
        this.symbol = symbol;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public char getSymbol() {
        return symbol;
    }

    public BrogueColor getColor() {
        return color;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setColor(BrogueColor color) {
        this.color = color;
    }
}
