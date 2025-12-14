package lucas.games.brogue.backend.grid;

public class Constants {
    public static final int COLS = 80;
    public static final int ROWS = 21;

    public static final Integer[][] NB_DIRS = {
            { 0, -1}, { 0, 1}, {-1,  0}, { 1, 0},
            {-1, -1}, {-1, 1}, { 1, -1}, { 1, 1}};
    public static final int[][] C_DIRS = {
            {0,  1}, { 1,  1}, { 1, 0}, { 1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1,  1}};

    public static final int MAX_DISTANCE = 30000;
    public static final int PDS_FORBIDDEN = -1;
    public static final int PDS_OBSTRUCTION = -2;

}
