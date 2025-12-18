import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public class Advanced extends Bot {
    private Stack<int[]> targets = new Stack<>(); 
    private int[] lastShot = null; 

    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;

        if (lastShot != null) {
            int r = lastShot[0];
            int c = lastShot[1];
            // Check if our last shot was a HIT
            if (enemyBoard[r][c] == Utils.HIT) {
                analyzeAndAddTargets(r, c, rows, cols, enemyBoard);
            }
        }

        int[] shotCoordinates;
        while (!targets.isEmpty()) {
            shotCoordinates = targets.pop();
            // Ensure we don't shoot where we already shot (< 0)
            if (enemyBoard[shotCoordinates[0]][shotCoordinates[1]] >= 0) {
                lastShot = shotCoordinates;
                return shotCoordinates;
            }
        }

        do {
            shotCoordinates = new int[]{random.nextInt(rows), random.nextInt(cols)};
        } while (enemyBoard[shotCoordinates[0]][shotCoordinates[1]] < 0);

        lastShot = shotCoordinates;
        return shotCoordinates;
    }

    private void analyzeAndAddTargets(int r, int c, int rows, int cols, int[][] board) {
        // Utils.isValid now takes 2 args, size is static inside Utils
        boolean isVertical = (Utils.isValid(r - 1, c) && board[r - 1][c] == Utils.HIT) ||
                             (Utils.isValid(r + 1, c) && board[r + 1][c] == Utils.HIT);

        boolean isHorizontal = (Utils.isValid(r, c - 1) && board[r][c - 1] == Utils.HIT) ||
                               (Utils.isValid(r, c + 1) && board[r][c + 1] == Utils.HIT);

        ArrayList<int[]> potentialMoves = new ArrayList<>();

        if (isVertical || isHorizontal) {
            targets.clear();
            if (isVertical) {
                addValidEnd(r, c, -1, 0, board, potentialMoves);
                addValidEnd(r, c,  1, 0, board, potentialMoves);
            } else { 
                addValidEnd(r, c, 0, -1, board, potentialMoves);
                addValidEnd(r, c, 0,  1, board, potentialMoves);
            }
        } else {
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int newR = r + d[0];
                int newC = c + d[1];
                if (Utils.isValid(newR, newC) && board[newR][newC] >= 0) {
                    potentialMoves.add(new int[]{newR, newC});
                }
            }
        }
        
        Collections.shuffle(potentialMoves);
        for (int[] move : potentialMoves) targets.push(move);
    }

    private void addValidEnd(int r, int c, int dr, int dc, int[][] board, ArrayList<int[]> moves) {
        int currR = r + dr;
        int currC = c + dc;

        // Skip over existing HITs
        while (Utils.isValid(currR, currC) && board[currR][currC] == Utils.HIT) {
            currR += dr;
            currC += dc;
        }

        // If we land on a valid unshot spot (>=0), add it
        if (Utils.isValid(currR, currC) && board[currR][currC] >= 0) {
            moves.add(new int[]{currR, currC});
        }
    }
}