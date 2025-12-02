package lucas.games.brogue.backend;

public class MonsterBehaviorFlags {
    public static final int IMMOBILE = 1 << 2;
    public static final int IMMUNE_TO_WEAPONS = 1 << 9;
    public static final int INVULNERABLE = 1 << 18;
    public static final int GETS_TURN_ON_ACTIVATION = 1 << 28;
}
