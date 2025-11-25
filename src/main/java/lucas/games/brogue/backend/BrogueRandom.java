package lucas.games.brogue.backend;

/**
 * <p>Brogue uses a Linear Congruential Generator (LCG) with specific constants
 * It is crucial to replicate this exactly to ensure that dungeon generation
 * matches the original C version for the same seeds.</p>
 *
 * Algorithm: next = seed * 1103515245 + 12345;
 */
public class BrogueRandom {

    private int seed;

    /**
     * Creates a new RNG with the given seed.
     * @param seed The initial seed (matches Brogue's unsigned int seed).
     */
    public BrogueRandom(int seed) {
        this.seed = seed;
    }

    /**
     * Creates a new RNG with a random seed based on system time.
     */
    public BrogueRandom() {
        this((int) System.currentTimeMillis());
    }

    /**
     * Returns the current internal seed state.
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Sets the seed manually.
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Generates the next raw 32-bit random number.
     * Matches C: rand_seed * 1103515245 + 12345;
     *
     * @return The raw random integer (can be negative due to Java's signed int,
     * but bits match C's unsigned int).
     */
    public int next() {
        seed = seed * 1103515245 + 12345;
        return seed;
    }

    /**
     * Returns a random integer between 0 (inclusive) and max (exclusive).
     * Mimics C: randomGenerator() % max
     *
     * @param max The upper bound (exclusive).
     * @return A number from 0 to max-1.
     */
    public int randomInteger(int max) {
        if (max <= 0) return 0;
        // We use toUnsignedLong to treat the 32-bit int as a positive number
        // before the modulo, ensure behaviour matches C's unsigned modulo.
        return (int) (Integer.toUnsignedLong(next()) % max);
    }

    /**
     * Returns a random integer between min and max (inclusive).
     *
     * @param min The lower bound (inclusive).
     * @param max The upper bound (inclusive).
     * @return A number between min and max.
     */
    public int randomRange(int min, int max) {
        if (max < min) return min;
        return min + randomInteger(max - min + 1);
    }

    /**
     * Returns true with the given percentage chance.
     *
     * @param percent Chance between 0 and 100.
     * @return true if the roll succeeds.
     */
    public boolean randomPercent(int percent) {
        return randomInteger(100) < percent;
    }

    /**
     * Returns a random double between 0.0 and 1.0.
     */
    public double randomFloat() {
        // Divide by 2^32 - 1 (maximum unsigned int value).
        return Integer.toUnsignedLong(next()) / (double) 0xFFFFFFFFL;
    }
}
