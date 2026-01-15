import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public class Advanced extends Bot {
    // chytry bot ktery dohledava lode
    private Stack<int[]> targets = new Stack<>(); 
    private int[] lastShot = null; 

    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;

        if (lastShot != null) {
            int r = lastShot[0];
            int c = lastShot[1];
            
            if (enemyBoard[r][c] == Utils.HIT) {
                analyzeAndAddTargets(r, c, rows, cols, enemyBoard);
            }
        }

        int[] shotCoordinates;
        while (!targets.isEmpty()) {
            shotCoordinates = targets.pop();
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

    // prida do seznamu cilu okolni policka
    private void analyzeAndAddTargets(int r, int c, int rows, int cols, int[][] board) {
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

    // pomocna metoda pro hledani konce lode
    private void addValidEnd(int r, int c, int dr, int dc, int[][] board, ArrayList<int[]> moves) {
        int currR = r + dr;
        int currC = c + dc;

        while (Utils.isValid(currR, currC) && board[currR][currC] == Utils.HIT) {
            currR += dr;
            currC += dc;
        }

        if (Utils.isValid(currR, currC) && board[currR][currC] >= 0) {
            moves.add(new int[]{currR, currC});
        }
    }
}