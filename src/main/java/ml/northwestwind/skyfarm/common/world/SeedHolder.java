package ml.northwestwind.skyfarm.common.world;

public class SeedHolder {
    private static long seed = 0;

    public static void setSeed(long seed) {
        SeedHolder.seed = seed;
    }

    public static long getSeed() {
        return seed;
    }
}
