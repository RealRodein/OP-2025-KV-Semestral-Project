import java.util.ArrayList;
import java.util.List;

public class Utils {
    // pomocna trida s konstantami
    public static final int WATER = 0;
    public static final int SHIP = 1;
    public static final int HIT = -1;
    public static final int MISS = -2;

    public static final int BOARD_SIZE = 10;

    // overi zda jsou souradnice uvnitr hraci plochy
    public static boolean isValid(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    // ziska vsechny casti lode pro kontrolu potopeni
    public static List<int[]> getShipParts(int[][] board, int startR, int startC) {
        List<int[]> parts = new ArrayList<>();
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        collectParts(board, startR, startC, parts, visited);
        return parts;
    }

    // rekurzivni vyhledani spojenych casti lode
    private static void collectParts(int[][] board, int r, int c, List<int[]> parts, boolean[][] visited) {
        if (!isValid(r, c) || visited[r][c]) return;

        int cell = board[r][c];
        if (cell == WATER || cell == MISS) return;

        visited[r][c] = true;
        parts.add(new int[]{r, c});

        collectParts(board, r + 1, c, parts, visited);
        collectParts(board, r - 1, c, parts, visited);
        collectParts(board, r, c + 1, parts, visited);
        collectParts(board, r, c - 1, parts, visited);
    }
}