package lucas.games.brogue.backend;

public class RogueMain {

    private Grid level;
    private Player player;

    /* Original C code:
// Seed is used as the dungeon seed unless it's zero, in which case generate a new one.
// Either way, previousGameSeed is set to the seed we use.
// None of this seed stuff is applicable if we're playing a recording.
void initializeRogue(uint64_t seed) {
    short i, j, k;
    item *theItem;
    boolean playingback, playbackFF, playbackPaused, wizard, easy, displayStealthRangeMode;
    boolean trueColorMode;
    boolean hideSeed;
    short oldRNG;
    char currentGamePath[BROGUE_FILENAME_MAX];

    playingback = rogue.playbackMode; // the only animals that need to go on the ark
    playbackPaused = rogue.playbackPaused;
    playbackFF = rogue.playbackFastForward;
    wizard = rogue.wizard;
    hideSeed = rogue.hideSeed;
    easy = rogue.easyMode;
    displayStealthRangeMode = rogue.displayStealthRangeMode;
    trueColorMode = rogue.trueColorMode;

    strcpy(currentGamePath, rogue.currentGamePath);

    if (rogue.meteredItems != NULL) {
        free(rogue.meteredItems);
    }

    memset((void *) &rogue, 0, sizeof( playerCharacter )); // the flood
    rogue.playbackMode = playingback;
    rogue.playbackPaused = playbackPaused;
    rogue.playbackFastForward = playbackFF;
    rogue.wizard = wizard;
    rogue.hideSeed = hideSeed;
    rogue.easyMode = easy;
    rogue.displayStealthRangeMode = displayStealthRangeMode;
    rogue.trueColorMode = trueColorMode;

    rogue.gameHasEnded = false;
    rogue.gameInProgress = true;
    rogue.highScoreSaved = false;
    rogue.cautiousMode = false;
    rogue.milliseconds = 0;

    rogue.meteredItems = calloc(gameConst->numberMeteredItems, sizeof(meteredItem));
    rogue.featRecord = calloc(gameConst->numberFeats, sizeof(boolean));
    strcpy(rogue.currentGamePath, currentGamePath);

    rogue.RNG = RNG_SUBSTANTIVE;
    if (!rogue.playbackMode) {
        rogue.seed = seedRandomGenerator(seed);
        previousGameSeed = rogue.seed;
    }

#ifdef SCREEN_UPDATE_BENCHMARK
    screen_update_benchmark();
#endif

    initRecording();

    levels = malloc(sizeof(levelData) * (gameConst->deepestLevel+1));
    levels[0].upStairsLoc.x = (DCOLS - 1) / 2 - 1;
    levels[0].upStairsLoc.y = DROWS - 2;

    // Set metered item frequencies to initial values.
    for (i = 0; i < gameConst->numberMeteredItems; i++) {
        rogue.meteredItems[i].frequency = meteredItemsGenerationTable[i].initialFrequency;
    }

    // all DF messages are eligible for display
    resetDFMessageEligibility();

    // initialize the levels list
    for (i=0; i<gameConst->deepestLevel+1; i++) {
        if (rogue.seed >> 32) {
            // generate a 64-bit seed
            levels[i].levelSeed = rand_64bits();
        } else {
            // backward-compatible seed
            levels[i].levelSeed = (unsigned long) rand_range(0, 9999);
            levels[i].levelSeed += (unsigned long) 10000 * rand_range(0, 9999);
        }
        if (levels[i].levelSeed == 0) { // seed 0 is not acceptable
            levels[i].levelSeed = i + 1;
        }
        levels[i].monsters = createCreatureList();;
        levels[i].dormantMonsters = createCreatureList();;
        levels[i].items = NULL;
        levels[i].scentMap = NULL;
        levels[i].visited = false;
        levels[i].playerExitedVia = (pos){ .x = 0, .y = 0 };
        do {
            levels[i].downStairsLoc.x = rand_range(1, DCOLS - 2);
            levels[i].downStairsLoc.y = rand_range(1, DROWS - 2);
        } while (distanceBetween(levels[i].upStairsLoc, levels[i].downStairsLoc) < DCOLS / 3);
        if (i < gameConst->deepestLevel) {
            levels[i+1].upStairsLoc.x = levels[i].downStairsLoc.x;
            levels[i+1].upStairsLoc.y = levels[i].downStairsLoc.y;
        }
    }

    // initialize the waypoints list
    for (i=0; i<MAX_WAYPOINT_COUNT; i++) {
        rogue.wpDistance[i] = allocGrid();
        fillGrid(rogue.wpDistance[i], 0);
    }

    rogue.rewardRoomsGenerated = 0;

    // pre-shuffle the random terrain colors
    oldRNG = rogue.RNG;
    rogue.RNG = RNG_COSMETIC;
    //assureCosmeticRNG;
    for (i=0; i<DCOLS; i++) {
        for( j=0; j<DROWS; j++ ) {
            for (k=0; k<8; k++) {
                terrainRandomValues[i][j][k] = rand_range(0, 1000);
            }
        }
    }
    restoreRNG;

    zeroOutGrid(displayDetail);

    for (i=0; i<NUMBER_MONSTER_KINDS; i++) {
        monsterCatalog[i].monsterID = i;
    }

    shuffleFlavors();

    for (i = 0; i < gameConst->numberFeats; i++) {
        rogue.featRecord[i] = featTable[i].initialValue;
    }

    deleteMessages();
    for (i = 0; i < MESSAGE_ARCHIVE_ENTRIES; i++) { // Clear the message archive.
        messageArchive[i].message[0] = '\0';
    }
    messageArchivePosition = 0;

    // Seed the stacks.
    floorItems = (item *) malloc(sizeof(item));
    memset(floorItems, '\0', sizeof(item));
    floorItems->nextItem = NULL;

    packItems = (item *) malloc(sizeof(item));
    memset(packItems, '\0', sizeof(item));
    packItems->nextItem = NULL;

    monsterItemsHopper = (item *) malloc(sizeof(item));
    memset(monsterItemsHopper, '\0', sizeof(item));
    monsterItemsHopper->nextItem = NULL;

    for (i = 0; i < MAX_ITEMS_IN_MONSTER_ITEMS_HOPPER; i++) {
        theItem = generateItem(ALL_ITEMS & ~FOOD, -1); // Monsters can't carry food: the food clock cannot be cheated!
        theItem->nextItem = monsterItemsHopper->nextItem;
        monsterItemsHopper->nextItem = theItem;
    }

    monsters = &levels[0].monsters;
    dormantMonsters = &levels[0].dormantMonsters;
    purgatory = createCreatureList();

    scentMap            = NULL;
    safetyMap           = allocGrid();
    allySafetyMap       = allocGrid();
    chokeMap            = allocGrid();

    rogue.mapToSafeTerrain = allocGrid();

    // Zero out the dynamic grids, as an essential safeguard against OOSes:
    fillGrid(safetyMap, 0);
    fillGrid(allySafetyMap, 0);
    fillGrid(chokeMap, 0);
    fillGrid(rogue.mapToSafeTerrain, 0);

    // initialize the player

    memset(&player, '\0', sizeof(creature));
    player.info = monsterCatalog[0];
    setPlayerDisplayChar();
    initializeGender(&player);
    player.movementSpeed = player.info.movementSpeed;
    player.attackSpeed = player.info.attackSpeed;
    initializeStatus(&player);
    player.carriedItem = NULL;
    player.currentHP = player.info.maxHP;
    player.creatureState = MONSTER_ALLY;
    player.ticksUntilTurn = 0;
    player.mutationIndex = -1;

    rogue.depthLevel = 1;
    rogue.deepestLevel = 1;
    rogue.scentTurnNumber = 1000;
    rogue.playerTurnNumber = 0;
    rogue.absoluteTurnNumber = 0;
    rogue.previousPoisonPercent = 0;
    rogue.foodSpawned = 0;
    rogue.gold = 0;
    rogue.goldGenerated = 0;
    rogue.disturbed = false;
    rogue.autoPlayingLevel = false;
    rogue.automationActive = false;
    rogue.justRested = false;
    rogue.justSearched = false;
    rogue.inWater = false;
    rogue.creaturesWillFlashThisTurn = false;
    rogue.updatedSafetyMapThisTurn = false;
    rogue.updatedAllySafetyMapThisTurn = false;
    rogue.updatedMapToSafeTerrainThisTurn = false;
    rogue.updatedMapToShoreThisTurn = false;
    rogue.strength = 12;
    rogue.weapon = NULL;
    rogue.armor = NULL;
    rogue.ringLeft = NULL;
    rogue.ringRight = NULL;
    rogue.swappedIn = NULL;
    rogue.swappedOut = NULL;
    rogue.monsterSpawnFuse = rand_range(125, 175);
    rogue.ticksTillUpdateEnvironment = 100;
    rogue.mapToShore = NULL;
    rogue.cursorLoc = INVALID_POS;
    rogue.xpxpThisTurn = 0;

    rogue.yendorWarden = NULL;

    rogue.flares = NULL;
    rogue.flareCount = rogue.flareCapacity = 0;

    rogue.minersLight = lightCatalog[MINERS_LIGHT];

    rogue.clairvoyance = rogue.regenerationBonus
    = rogue.stealthBonus = rogue.transference = rogue.wisdomBonus = rogue.reaping = 0;
    rogue.lightMultiplier = 1;

    theItem = generateItem(FOOD, RATION);
    theItem = addItemToPack(theItem);

    theItem = generateItem(WEAPON, DAGGER);
    theItem->enchant1 = theItem->enchant2 = 0;
    theItem->terrainFlags &= ~(ITEM_CURSED | ITEM_RUNIC);
    identify(theItem);
    theItem = addItemToPack(theItem);
    equipItem(theItem, false, NULL);

    theItem = generateItem(WEAPON, DART);
    theItem->enchant1 = theItem->enchant2 = 0;
    theItem->quantity = 15;
    theItem->terrainFlags &= ~(ITEM_CURSED | ITEM_RUNIC);
    identify(theItem);
    theItem = addItemToPack(theItem);

    theItem = generateItem(ARMOR, LEATHER_ARMOR);
    theItem->enchant1 = 0;
    theItem->terrainFlags &= ~(ITEM_CURSED | ITEM_RUNIC);
    identify(theItem);
    theItem = addItemToPack(theItem);
    equipItem(theItem, false, NULL);
    player.status[STATUS_DONNING] = 0;

    recalculateEquipmentBonuses();

    if (D_OMNISCENCE) {
        rogue.playbackOmniscience = 1;
    }

    DEBUG {
        theItem = generateItem(RING, RING_CLAIRVOYANCE);
        theItem->enchant1 = max(DROWS, DCOLS);
        theItem->terrainFlags &= ~ITEM_CURSED;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(RING, RING_AWARENESS);
        theItem->enchant1 = 30;
        theItem->terrainFlags &= ~ITEM_CURSED;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(WEAPON, DAGGER);
        theItem->enchant1 = 50;
        theItem->enchant2 = W_QUIETUS;
        theItem->terrainFlags &= ~(ITEM_CURSED);
        theItem->terrainFlags |= (ITEM_PROTECTED | ITEM_RUNIC | ITEM_RUNIC_HINTED);
        theItem->damage.lowerBound = theItem->damage.upperBound = 25;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(ARMOR, LEATHER_ARMOR);
        theItem->enchant1 = 50;
        theItem->enchant2 = A_REFLECTION;
        theItem->terrainFlags &= ~(ITEM_CURSED | ITEM_RUNIC_HINTED);
        theItem->terrainFlags |= (ITEM_PROTECTED | ITEM_RUNIC);
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(STAFF, STAFF_FIRE);
        theItem->enchant1 = 10;
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(STAFF, STAFF_LIGHTNING);
        theItem->enchant1 = 10;
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(STAFF, STAFF_TUNNELING);
        theItem->enchant1 = 10;
        theItem->charges = 3000;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(STAFF, STAFF_OBSTRUCTION);
        theItem->enchant1 = 10;
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(STAFF, STAFF_ENTRANCEMENT);
        theItem->enchant1 = 10;
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(WAND, WAND_BECKONING);
        theItem->charges = 3000;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(WAND, WAND_DOMINATION);
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(WAND, WAND_PLENTY);
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

        theItem = generateItem(WAND, WAND_NEGATION);
        theItem->charges = 300;
        identify(theItem);
        theItem = addItemToPack(theItem);

    }
    clearMessageArchive();
    blackOutScreen();
    welcome();
}
     */

    private void initializeRogue(int seed) {}

    public void startLevel(int oldLevelNumber, int stairDirection) {
        if (oldLevelNumber == Constants.DEEPEST_LEVEL && stairDirection != -1) {
            return;
        }

//        synchronizePlayerTimeState();

//        player.updatedSafetyMapThisTurn = false;
//        player.updatedAllySafetyMapThisTurn = false;
//        player.updatedMapToSafeTerrainThisTurn = false;

//        player.cursorLoc = INVALID_POS;
//        player.lastTarget = null;

//        boolean connectingStairsDiscovered = playerMapAt(player.downLoc).discovered() || playerMapAt(player.downLoc).magic_mapped();
//        if (stairDirection == 0) { // fallen
//            levels[oldLevelNumber - 1].playerExitedVia = player.position;
//        }

        if (oldLevelNumber != player.depthLevel) {
            int playerX = player.position.x();
            int playerY = player.position.y();
            if (level.tileHasFlag(player.position, TerrainFlags.AUTO_DESCENT)) {
                for (int i = 0; i < 8; i++) {
                    if (!level.tileHasFlag(new Position(
                            player.position.x() + Constants.nbDirs[i][0],
                            player.position.y() + Constants.nbDirs[i][1]),
                            TerrainFlags.PATHING_BLOCKER)) {
                        playerX = player.position.x() + Constants.nbDirs[i][0];
                        playerY = player.position.y() + Constants.nbDirs[i][1];
                        break;
                    }
                }
            }
            Grid mapToStairs = level;
            mapToStairs = mapToStairs.edit().fillGrid(new Tile()).build();
            for (int flying = 0; flying <= 1; flying++) {
                mapToStairs = mapToStairs.edit().fillGrid(new Tile()).build();
                mapToStairs.calculateDistances(new Position(playerX, playerY),
                        (flying == 0 ? TerrainFlags.OBSTRUCTS_PASSABILITY : TerrainFlags.PATHING_BLOCKER) | TerrainFlags.SACRED_GROUND,
                        null,
                        true,
                        true);
            }
        }
    }
}
