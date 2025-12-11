package lucas.games.brogue.backend;

public class Random {

    private int seed;

    public Random(int seed) {
        this.seed = seed;
    }

    public Random() {
        this((int) System.currentTimeMillis());
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int next() {
        seed = seed * 1103515245 + 12345;
        return seed;
    }

    public int randomInt(int max) {
        if (max <= 0) return 0;
        return (int) (Integer.toUnsignedLong(next()) % max);
    }

    public int randomRange(int min, int max) {
        if (max < min) return min;
        return min + randomInt(max - min + 1);
    }

    public boolean randomPercent(int percent) {
        return randomInt(100) < percent;
    }

    public double randomFloat() {
        return (double) Integer.toUnsignedLong(next()) / (double) Integer.toUnsignedLong(0xFFFFFFFF);
    }
}
