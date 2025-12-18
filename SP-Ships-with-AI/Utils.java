import java.util.ArrayList;
import java.util.List;

public class Utils {
    // --- Constants for Readability ---
    public static final int WATER = 0;
    public static final int SHIP = 1;
    public static final int HIT = -1;
    public static final int MISS = -2;

    public static final int BOARD_SIZE = 10;

    // Checks if a point is on the board
    public static boolean isValid(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    /**
     * Finds a connected component of ship parts (either Intact or Hit).
     * Used by Logic (to check sunk status) and Expert Bot (to target ships).
     */
    public static List<int[]> getShipParts(int[][] board, int startR, int startC) {
        List<int[]> parts = new ArrayList<>();
        // Helper to track visited cells during recursion
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        collectParts(board, startR, startC, parts, visited);
        return parts;
    }

    private static void collectParts(int[][] board, int r, int c, List<int[]> parts, boolean[][] visited) {
        if (!isValid(r, c) || visited[r][c]) return;

        int cell = board[r][c];
        // Stop if we hit Water or a Miss (Only traverse Ship(1) or Hit(-1))
        if (cell == WATER || cell == MISS) return;

        visited[r][c] = true;
        parts.add(new int[]{r, c});

        collectParts(board, r + 1, c, parts, visited);
        collectParts(board, r - 1, c, parts, visited);
        collectParts(board, r, c + 1, parts, visited);
        collectParts(board, r, c - 1, parts, visited);
    }
}