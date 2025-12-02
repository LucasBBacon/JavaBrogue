package lucas.games.brogue.backend;

import java.util.List;
import java.util.function.Predicate;

public class DijkstraMap {

    private static final int PDS_FORBIDDEN = -1;

    private DijkstraMap(Grid grid, List<Position> goals, Predicate<Tile> isBlocked, boolean allowDiagonals) {
        for (int i = 0; i < Constants.DCOLS; i++) {
            for (int j = 0; j < Constants.DROWS; j++) {
                int cost;
                Creature monster = grid.monsterAtLocation(new Position(i, j));
                if (monster != null
                    && (monster.info.flags() & (MonsterBehaviorFlags.IMMUNE_TO_WEAPONS | MonsterBehaviorFlags.INVULNERABLE)) != 0
                    && (monster.info.flags() & (MonsterBehaviorFlags.IMMOBILE | MonsterBehaviorFlags.GETS_TURN_ON_ACTIVATION)) != 0
                ) {
                    // Always avoid damage-immune stationary monsters
                    cost = PDS_FORBIDDEN;
                } else if {

                }
            }
        }
    }

    public static DijkstraMap forWalking(Grid grid, List<Position> goals) {
        return new DijkstraMap(grid, goals, Tile::pathingBlocker, true);
    }
}
