package lucas.games.brogue.backend;

public class Creature extends Actor {

    public CreatureType info;
    private Position loc;
    private int depth;
    private int currentHP;
    private int turnsUntilRegen;
    private int regenPerTurn;               // Number of HP to regenerate every single turn
    private int weaknessAmount;             // Number of points of weakness that are inflicted by the weakness status effect
    private int poisonAmount;               // Number of points of damage per turn from poison
    private CreatureStates creatureStates;  // Current behavioral state
    private CreatureMode creatureMode;      // Current behavioral mode (higher-level than state)

    private int mutationIndex;              // What the mutation monster has (-1 for none)
    private boolean wasNegated;

    // Waypoints:
    int targetWaypointIndex;                // The index number of the waypoint the creature is pathing toward
    private boolean[] waypointAlreadyVisited; // Checklist of waypoints
    private Position lastSeenPlayerAt;      // Last location at which monster hunted the player

    private Position targetCorpseLoc;       // Location of the corpse that the monster is approaching to gain its abilities
    private String targetCorpseName;        // Name of the deceased monster whose corpse is being approached
    private int absorptionFlags;            // Ability/behaviour flags that the monster will gain when absorption is complete
    private boolean absorptionBehavior;     // Behaviour instead of ability
    private int absorptionBolt;             // Bolt index that the monster will learn to cast when absorption is complete
    private int corpseAbsorptionCounter;    // Used to measure both the time until the monster stops being interested in the corpse,
                                            // and, later, the time until the monster finishes absorbing the corpse
    private int[][] mapToMe;                // If a pack leader, this is a periodically updated pathing map to get to the leader
    private int[][] safetyMap;              // Fleeing monsters store their own safety map when out of player FOV to avoid omniscience
    private int ticksUntilTurn;             // How long before the creature gets its next move

    // Locally cached statistics that may be temporarily modified by status effects
    private int movementSpeed;
    private int attackSpeed;

    private int previousHealthPoints;       // Remembers what your health proportion was at the start of the turn
    private int turnsSpentStationary;       // How many (subjective) turns it's been since the creature moved between tiles
    private int flashStrength;              // Monster will flash soon - this indicates the percent strength of the flash
    private BrogueColor flashColor;         // The color the monster will flash
    private int[] status;
    private int[] maxStatus;                // Used to set the max point on the status bars
    private int bookkeepingFlags;
    private int spawnDepth;                 // Keep track of the depth of the machine to which they relate (for activation monsters)
    private int machineHome;                // Monsters that spawn in the machine keep track of the machine number here (for activation monsters)
    private int xpxp;                       // Exploration experience (used to time telepathic bonding for allies)
    private int newPowerCount;              // How many more times this monster can absorb a fallen monster
    private int totalPowerCount;            // How many times has the monster been empowered? Used to recover abilities when negated

    private Creature[] leader;              // Only if monster is a follower
    private Creature[] carriedMonster;      // When vampires turn to bats, one of the bats restore the vampire when it dies
    private Item[] carriedItem;             // Only used for monsters
}
