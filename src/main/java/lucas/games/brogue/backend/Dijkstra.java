package lucas.games.brogue.backend;

/*
Dijkstra algorithm from C:
#include "Rogue.h"
#include "GlobalsBase.h"
#include "Globals.h"

typedef struct pdsLink {
    short distance;
    short cost;
    struct pdsLink *left;
    struct pdsLink *right;
} pdsLink;

typedef struct pdsMap {
    pdsLink front;
    pdsLink links[DCOLS * DROWS];
} pdsMap;

static void pdsUpdate(pdsMap *map, boolean useDiagonals) {
    short dirs = useDiagonals ? 8 : 4;

    pdsLink *head = map->front.right;
    map->front.right = NULL;

    while (head != NULL) {
        for (short dir = 0; dir < dirs; dir++) {
            pdsLink *link = head + (nbDirs[dir][0] + DCOLS * nbDirs[dir][1]);
            if (link < map->links || link >= map->links + DCOLS * DROWS) continue;

            // verify passability
            if (link->cost < 0) continue;
            if (dir >= 4) {
                pdsLink *way1 = head + nbDirs[dir][0];
                pdsLink *way2 = head + DCOLS * nbDirs[dir][1];
                if (way1->cost == PDS_OBSTRUCTION || way2->cost == PDS_OBSTRUCTION) continue;
            }

            if (head->distance + link->cost < link->distance) {
                link->distance = head->distance + link->cost;

                // reinsert the touched cell; it'll be close to the beginning of the list now, so
                // this will be very fast.  start by removing it.

                if (link->right != NULL) link->right->left = link->left;
                if (link->left != NULL) link->left->right = link->right;

                pdsLink *left = head;
                pdsLink *right = head->right;
                while (right != NULL && right->distance < link->distance) {
                    left = right;
                    right = right->right;
                }
                if (left != NULL) left->right = link;
                link->right = right;
                link->left = left;
                if (right != NULL) right->left = link;
            }
        }

        pdsLink *right = head->right;

        head->left = NULL;
        head->right = NULL;

        head = right;
    }
}

static void pdsClear(pdsMap *map, short maxDistance) {
    map->front.right = NULL;

    for (int i=0; i < DCOLS*DROWS; i++) {
        map->links[i].distance = maxDistance;
        map->links[i].left = NULL;
        map->links[i].right = NULL;
    }
}

static void pdsSetDistance(pdsMap *map, short x, short y, short distance) {
    if (x > 0 && y > 0 && x < DCOLS - 1 && y < DROWS - 1) {
        pdsLink *link = PDS_CELL(map, x, y);
        if (link->distance > distance) {
            link->distance = distance;

            if (link->right != NULL) link->right->left = link->left;
            if (link->left != NULL) link->left->right = link->right;

            pdsLink *left = &map->front;
            pdsLink *right = map->front.right;

            while (right != NULL && right->distance < link->distance) {
                left = right;
                right = right->right;
            }

            link->right = right;
            link->left = left;
            left->right = link;
            if (right != NULL) right->left = link;
        }
    }
}

static void pdsBatchInput(pdsMap *map, short **distanceMap, short **costMap, short maxDistance) {
    pdsLink *left = NULL;
    pdsLink *right = NULL;

    map->front.right = NULL;
    for (int i=0; i<DCOLS; i++) {
        for (int j=0; j<DROWS; j++) {
            pdsLink *link = PDS_CELL(map, i, j);

            if (distanceMap != NULL) {
                link->distance = distanceMap[i][j];
            } else {
                if (costMap != NULL) {
                    // totally hackish; refactor
                    link->distance = maxDistance;
                }
            }

            int cost;

            if (i == 0 || j == 0 || i == DCOLS - 1 || j == DROWS - 1) {
                cost = PDS_OBSTRUCTION;
            } else if (costMap == NULL) {
                if (cellHasTerrainFlag((pos){ i, j }, T_OBSTRUCTS_PASSABILITY) && cellHasTerrainFlag((pos){ i, j }, T_OBSTRUCTS_DIAGONAL_MOVEMENT)) cost = PDS_OBSTRUCTION;
                else cost = PDS_FORBIDDEN;
            } else {
                cost = costMap[i][j];
            }

            link->cost = cost;

            if (cost > 0) {
                if (link->distance < maxDistance) {
                    if (right == NULL || right->distance > link->distance) {
                        // left and right are used to traverse the list; if many cells have similar values,
                        // some time can be saved by not clearing them with each insertion.  this time,
                        // sadly, we have to start from the front.

                        left = &map->front;
                        right = map->front.right;
                    }

                    while (right != NULL && right->distance < link->distance) {
                        left = right;
                        right = right->right;
                    }

                    link->right = right;
                    link->left = left;
                    left->right = link;
                    if (right != NULL) right->left = link;

                    left = link;
                } else {
                    link->right = NULL;
                    link->left = NULL;
                }
            } else {
                link->right = NULL;
                link->left = NULL;
            }
        }
    }
}

static void pdsBatchOutput(pdsMap *map, short **distanceMap, boolean useDiagonals) {
    pdsUpdate(map, useDiagonals);
    // transfer results to the distanceMap
    for (int i=0; i<DCOLS; i++) {
        for (int j=0; j<DROWS; j++) {
            distanceMap[i][j] = PDS_CELL(map, i, j)->distance;
        }
    }
}

void dijkstraScan(short **distanceMap, short **costMap, boolean useDiagonals) {
    static pdsMap map;

    pdsBatchInput(&map, distanceMap, costMap, 30000);
    pdsBatchOutput(&map, distanceMap, useDiagonals);
}

void calculateDistances(short **distanceMap,
                        short destinationX, short destinationY,
                        unsigned long blockingTerrainFlags,
                        creature *traveler,
                        boolean canUseSecretDoors,
                        boolean eightWays) {
    static pdsMap map;

    for (int i=0; i<DCOLS; i++) {
        for (int j=0; j<DROWS; j++) {
            signed char cost;
            creature *monst = monsterAtLoc((pos){ i, j });
            if (monst
                    && (monst->info.flags & (MONST_IMMUNE_TO_WEAPONS | MONST_INVULNERABLE))
                    && (monst->info.flags & (MONST_IMMOBILE | MONST_GETS_TURN_ON_ACTIVATION))) {

                // Always avoid damage-immune stationary monsters.
                cost = PDS_FORBIDDEN;
            } else if (canUseSecretDoors
                    && cellHasTMFlag((pos){ i, j }, TM_IS_SECRET)
                    && cellHasTerrainFlag((pos){ i, j }, T_OBSTRUCTS_PASSABILITY)
                    && !(discoveredTerrainFlagsAtLoc((pos){ i, j }) & T_OBSTRUCTS_PASSABILITY)) {

                cost = 1;
            } else if (cellHasTerrainFlag((pos){ i, j }, T_OBSTRUCTS_PASSABILITY)
                    || (traveler && traveler == &player && !(pmap[i][j].flags & (DISCOVERED | MAGIC_MAPPED)))) {

                cost = cellHasTerrainFlag((pos){ i, j }, T_OBSTRUCTS_DIAGONAL_MOVEMENT) ? PDS_OBSTRUCTION : PDS_FORBIDDEN;
            } else if ((traveler && monsterAvoids(traveler, (pos){i, j})) || cellHasTerrainFlag((pos){ i, j }, blockingTerrainFlags)) {
                cost = PDS_FORBIDDEN;
            } else {
                cost = 1;
            }

            PDS_CELL(&map, i, j)->cost = cost;
        }
    }

    pdsClear(&map, 30000);
    pdsSetDistance(&map, destinationX, destinationY, 0);
    pdsBatchOutput(&map, distanceMap, eightWays);
}

short pathingDistance(short x1, short y1, short x2, short y2, unsigned long blockingTerrainFlags) {
    short **distanceMap = allocGrid();
    calculateDistances(distanceMap, x2, y2, blockingTerrainFlags, NULL, true, true);
    short retval = distanceMap[x1][y1];
    freeGrid(distanceMap);
    return retval;
}


 */

public class Dijkstra {

    public static final int D_COLS = 79;
    public static final int D_ROWS = 29;
    public static final int PDS_OBSTRUCTION = -2;

    private static final int[][] NB_DIRS = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};


    public static class PDSLink {
        public int distance;
        public int cost;
        public PDSLink left;
        public PDSLink right;
        private final int index;

        public PDSLink(int index) {
            this.index = index;
        }
    }

    public static class PDSMap {
        public final PDSLink front;
        public final PDSLink[] links;

        public PDSMap() {
            this.front = new PDSLink(-1);
            this.links = new PDSLink[D_COLS * D_ROWS];
            for (int i = 0; i < links.length; i++) {
                links[i] = new PDSLink(i);
            }
        }
    }

    private final PDSMap map;

    public Dijkstra() {
        this.map = new PDSMap();
    }

    public void pdsUpdate(boolean useDiagonals) {
        int dirs = useDiagonals ? 8 : 4;

        PDSLink head = map.front.right;
        map.front.right = null;

        while (head != null) {
            int headIndex = head.index;
            int headCol = headIndex % D_COLS;

            for (int dir = 0; dir < dirs; dir++) {
                int neighbourCol = headCol + NB_DIRS[dir][0];
                if (neighbourCol < 0 || neighbourCol >= D_COLS) continue;

                int linkIndex = headIndex + NB_DIRS[dir][0] + D_COLS * NB_DIRS[dir][1];
                if (linkIndex < 0 || linkIndex >= map.links.length) continue;

                PDSLink link = map.links[linkIndex];
                if (link.cost < 0) continue;

                if (dir >= 4) {
                    int way1Index = headIndex + NB_DIRS[dir][0];
                    int way2Index = headIndex + D_COLS * NB_DIRS[dir][1];
                    // No need for explicit bounds check on way1/way2 as they are subsets of linkIndex which is already checked

                    PDSLink way1 = map.links[way1Index];
                    PDSLink way2 = map.links[way2Index];
                    if (way1.cost == PDS_OBSTRUCTION || way2.cost == PDS_OBSTRUCTION) continue;
                }

                if (head.distance + link.cost < link.distance) {
                    link.distance = head.distance + link.cost;

                    // Re-insert the touched cell into the sorted list
                    // First, remove it from its current position
                    if (link.right != null) {
                        link.right.left = link.left;
                    }
                    if (link.left != null) {
                        link.left.right = link.right;
                    }

                    // Find a new position and insert
                    PDSLink left = head;
                    PDSLink right = head.right;
                    while (right != null && right.distance < link.distance) {
                        left = right;
                        right = right.right;
                    }
                    left.right = link;
                    link.right = right;
                    link.left = left;
                    if (right != null) {
                        right.left = link;
                    }
                }
            }

            PDSLink nextHead = head.right;

            head.left = null;
            head.right = null;

            head = nextHead;
        }
    }

    public void pdsClear(int maxDistance) {
        map.front.right = null;

        for (int i = 0; i < D_COLS * D_ROWS; i++) {
            map.links[i].distance = maxDistance;
            map.links[i].left = null;
            map.links[i].right = null;
        }
    }

    public void pdsSetDistance(int col, int row, int distance) {
        if (col > 0 && row > 0 && col < D_COLS - 1 && row < D_ROWS - 1) {
            PDSLink link = map.links[col + row * D_COLS];
            if (link.distance > distance) {
                link.distance = distance;

                if (link.right != null) {
                    link.right.left = link.left;
                }
                if (link.left != null) {
                    link.left.right = link.right;
                }

                PDSLink left = map.front;
                PDSLink right = map.front.right;

                while (right != null && right.distance < link.distance) {
                    left = right;
                    right = right.right;
                }

                link.right = right;
                link.left = left;
                left.right = link;
                if (right != null) {
                    right.left = link;
                }
            }
        }
    }

    public void pdsBatchInput(TileGrid tileGrid, Integer[][] distanceMap, Integer[][] costMap, int maxDistance) {
        PDSLink left = null;
        PDSLink right = null;

        map.front.right = null;
        for (int i = 0; i < D_COLS; i++) {
            for (int j = 0; j < D_ROWS; j++) {
                PDSLink link = map.links[i + j * D_COLS];

                if (distanceMap != null) {
                    link.distance = distanceMap[i][j];
                } else {
                    if (costMap != null) {
                        // totally hackish; refactor
                        link.distance = maxDistance;
                    }
                }

                int cost;
                if (i == 0 || j == 0 || i == D_COLS - 1 || j == D_ROWS - 1) {
                    cost = PDS_OBSTRUCTION;
                } else if (costMap == null) {
                    if ()
                }
            }
        }
    }
}
