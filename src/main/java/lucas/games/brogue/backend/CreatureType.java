package lucas.games.brogue.backend;

public record CreatureType(
        MonsterTypes monsterID,
        char monsterName,
        DisplayGlyph glyph,
        BrogueColor foregroundColor,
        int maxHP,
        int defense,
        int accuracy,
        int damage,
        long turnsBetweenRegen, // turns to wait before gaining 1 HP
        int movementSpeed,
        int attackSpeed,
        DungeonFeatureTypes bloodType,
        LightType intrinsicLightType,
        boolean isLarge, // used for size of psychic emanation
        int dfChance, // percent chance to spawn the dungeon feature per awake turn
        DungeonFeatureTypes dfType, // Kind of dungeon feature
        BoltType[] bolts,
        int flags,
        long abilityFlags
) {

    public CreatureType(MonsterTypes monsterID,
                        char monsterName,
                        DisplayGlyph glyph,
                        BrogueColor foregroundColor,
                        int maxHP,
                        int defense,
                        int accuracy,
                        int damage,
                        long turnsBetweenRegen,
                        int movementSpeed,
                        int attackSpeed,
                        DungeonFeatureTypes bloodType,
                        LightType intrinsicLightType,
                        boolean isLarge,
                        int dfChance,
                        DungeonFeatureTypes dfType,
                        BoltType[] bolts,
                        int flags,
                        long abilityFlags) {
        this.monsterID = monsterID;
        this.monsterName = monsterName;
        this.glyph = glyph;
        this.foregroundColor = foregroundColor;
        this.maxHP = maxHP;
        this.defense = defense;
        this.accuracy = accuracy;
        this.damage = damage;
        this.turnsBetweenRegen = turnsBetweenRegen;
        this.movementSpeed = movementSpeed;
        this.attackSpeed = attackSpeed;
        this.bloodType = bloodType;
        this.intrinsicLightType = intrinsicLightType;
        this.isLarge = isLarge;
        this.dfChance = dfChance;
        this.dfType = dfType;
        this.bolts = bolts;
        this.flags = flags;
        this.abilityFlags = abilityFlags;
    }

    public static final class Builder {

        private MonsterTypes monsterID;
        private char monsterName;
        private DisplayGlyph glyph;
        private BrogueColor foregroundColor;
        private int maxHP;
        private int defense;
        private int accuracy;
        private int damage;
        private long turnsBetweenRegen;
        private int movementSpeed;
        private int attackSpeed;
        private DungeonFeatureTypes bloodType;
        private LightType intrinsicLightType;
        private boolean isLarge;
        private int dfChance;
        private DungeonFeatureTypes dfType;
        private BoltType[] bolts;
        private int flags;
        private long abilityFlags;

        public Builder withMonsterID(MonsterTypes monsterID) {
            this.monsterID = monsterID;
            return this;
        }

        public Builder withMonsterName(char monsterName) {
            this.monsterName = monsterName;
            return this;
        }

        public Builder withGlyph(DisplayGlyph glyph) {
            this.glyph = glyph;
            return this;
        }

        public Builder withForegroundColor(BrogueColor foregroundColor) {
            this.foregroundColor = foregroundColor;
            return this;
        }

        public Builder withMaxHP(int maxHP) {
            this.maxHP = maxHP;
            return this;
        }

        public Builder withDefense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder withAccuracy(int accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder withDamage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder withTurnsBetweenRegen(long turnsBetweenRegen) {
            this.turnsBetweenRegen = turnsBetweenRegen;
            return this;
        }

        public Builder withMovementSpeed(int movementSpeed) {
            this.movementSpeed = movementSpeed;
            return this;
        }

        public Builder withAttackSpeed(int attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        public Builder withBloodType(DungeonFeatureTypes bloodType) {
            this.bloodType = bloodType;
            return this;
        }

        public Builder withIntrinsicLightType(LightType intrinsicLightType) {
            this.intrinsicLightType = intrinsicLightType;
            return this;
        }

        public Builder withIsLarge(boolean isLarge) {
            this.isLarge = isLarge;
            return this;
        }

        public Builder withDfChance(int dfChance) {
            this.dfChance = dfChance;
            return this;
        }

        public Builder withDfType(DungeonFeatureTypes dfType) {
            this.dfType = dfType;
            return this;
        }

        public Builder withBolts(BoltType[] bolts) {
            this.bolts = bolts;
            return this;
        }

        public Builder withFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder withAbilityFlags(long abilityFlags) {
            this.abilityFlags = abilityFlags;
            return this;
        }

        public CreatureType build() {
            return new CreatureType(
                    this.monsterID,
                    this.monsterName,
                    this.glyph,
                    this.foregroundColor,
                    this.maxHP,
                    this.defense,
                    this.accuracy,
                    this.damage,
                    this.turnsBetweenRegen,
                    this.movementSpeed,
                    this.attackSpeed,
                    this.bloodType,
                    this.intrinsicLightType,
                    this.isLarge,
                    this.dfChance,
                    this.dfType,
                    this.bolts,
                    this.flags,
                    this.abilityFlags
            );
        }
    }
}
