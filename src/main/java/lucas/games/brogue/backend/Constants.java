package lucas.games.brogue.backend;

public final class Constants {

    private Constants() {}

    public static final int[][] nbDirs = {
            { 0, -1}, { 0,  1}, {-1,  0}, { 1,  0},
            {-1, -1}, {-1,  1}, { 1, -1}, { 1,  1}
    };

    public static final int DEEPEST_LEVEL = 30; // deepest player level

    public static final int MESSAGE_LINES = 3;
    public static final int COLS = 100;
    public static final int ROWS = 31 + MESSAGE_LINES;

    public static final int STAT_BAR_WIDTH = 20;

    public static final int DCOLS = COLS - STAT_BAR_WIDTH - 1;
    public static final int DROWS = ROWS - MESSAGE_LINES - 2;

}
