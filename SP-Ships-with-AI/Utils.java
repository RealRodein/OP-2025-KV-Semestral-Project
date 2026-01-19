import java.util.ArrayList;
import java.util.List;

public class Utils {
    // pomocna trida s konstantami a statickymi metodami
    public static final int WATER = 0;
    public static final int SHIP = 1;
    public static final int HIT = -1;
    public static final int MISS = -2;

    public static final int BOARD_SIZE = 10;

    // overi zda jsou zadane souradnice platne (uvnitr desky)
    public static boolean isValid(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    // vrati seznam souradnic vsech casti lode na dane pozici
    // pouziva se pro kontrolu potopeni
    public static List<int[]> getShipParts(int[][] board, int startR, int startC) {
        List<int[]> parts = new ArrayList<>();
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        collectParts(board, startR, startC, parts, visited);
        return parts;
    }

    // rekurzivni vyhledani spojenych casti lode (flood fill)
    private static void collectParts(int[][] board, int r, int c, List<int[]> parts, boolean[][] visited) {
        if (!isValid(r, c) || visited[r][c]) return;

        int cell = board[r][c];
        // zastavime se pokud narazime na vodu nebo strelu vedle
        if (cell == WATER || cell == MISS) return;

        visited[r][c] = true;
        parts.add(new int[]{r, c});

        // prohledani vsech ctyr smeru
        collectParts(board, r + 1, c, parts, visited);
        collectParts(board, r - 1, c, parts, visited);
        collectParts(board, r, c + 1, parts, visited);
        collectParts(board, r, c - 1, parts, visited);
    }
}