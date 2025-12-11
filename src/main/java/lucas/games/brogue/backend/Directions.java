package lucas.games.brogue.backend;

public enum Directions {

    NO_DIRECTION(-1),
    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3),
    UP_LEFT(4),
    DOWN_LEFT(5),
    UP_RIGHT(6),
    DOWN_RIGHT(7),
    DIRECTION_COUNT(8);

    public final int value;

    Directions(int value) {
        this.value = value;
    }
}
