package lucas.games.brogue.backend.data;

import lucas.games.brogue.backend.BrogueRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * A weighted list of spawn possibilities for a given depth range.
 */
public class SpawnTable {

    private record Entry(MonsterTemplate template, int minDepth, int maxDepth, int weight) {}

    private final List<Entry> entries = new ArrayList<>();

    public void add(MonsterTemplate template, int minDepth, int maxDepth, int weight) {
        entries.add(new Entry(template, minDepth, maxDepth, weight));
    }

    /**
     * Picks a random monster template suitable for the given depth.
     */
    public MonsterTemplate roll(int currentDepth, BrogueRandom rng) {
        // 1. Filter candidates based on depth
        List<Entry> candidates = new ArrayList<>();
        int totalWeight = 0;

        for (Entry e : entries) {
            if (currentDepth >= e.minDepth && currentDepth <= e.maxDepth) {
                candidates.add(e);
                totalWeight += e.weight;
            }
        }

        if (candidates.isEmpty()) return null;

        // 2. Weighted random selection
        int roll = rng.randomInteger(totalWeight);
        int current = 0;

        for (Entry e : candidates) {
            current += e.weight;
            if (roll < current) {
                return e.template;
            }
        }

        return candidates.getFirst().template(); // fallback
    }
}
