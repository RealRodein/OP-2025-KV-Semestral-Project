import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public class Advanced extends Bot {
    // pokrocily bot s logikou pro dohledavani lodi
    // pouziva zasobnik pro ukladani potencialnich cilu
    private Stack<int[]> targets = new Stack<>(); 
    private int[] lastShot = null; 

    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;

        // pokud byl predchozi tah uspesny, analyzujeme situaci
        if (lastShot != null) {
            int r = lastShot[0];
            int c = lastShot[1];
            
            if (enemyBoard[r][c] == Utils.HIT) {
                analyzeAndAddTargets(r, c, enemyBoard);
            }
        }

        // pokud mame cile v zasobniku, strilime na ne
        int[] shotCoordinates;
        while (!targets.isEmpty()) {
            shotCoordinates = targets.pop();
            // overime zda je cil stale platny (nepouzity)
            if (enemyBoard[shotCoordinates[0]][shotCoordinates[1]] >= 0) {
                lastShot = shotCoordinates;
                return shotCoordinates;
            }
        }

        // pokud nemame zadne prioritni cile, strilime nahodne
        do {
            shotCoordinates = new int[]{random.nextInt(rows), random.nextInt(cols)};
        } while (enemyBoard[shotCoordinates[0]][shotCoordinates[1]] < 0);

        lastShot = shotCoordinates;
        return shotCoordinates;
    }

    // prida okolni policka zasazene lode do seznamu cilu
    private void analyzeAndAddTargets(int r, int c, int[][] board) {
        // detekce orientace lode podle sousednich zasahu
        boolean isVertical = (Utils.isValid(r - 1, c) && board[r - 1][c] == Utils.HIT) ||
                             (Utils.isValid(r + 1, c) && board[r + 1][c] == Utils.HIT);

        boolean isHorizontal = (Utils.isValid(r, c - 1) && board[r][c - 1] == Utils.HIT) ||
                               (Utils.isValid(r, c + 1) && board[r][c + 1] == Utils.HIT);

        ArrayList<int[]> potentialMoves = new ArrayList<>();

        if (isVertical || isHorizontal) {
            // pokud zname smer, zamerime se jen na konce linie
            targets.clear();
            if (isVertical) {
                addValidEnd(r, c, -1, 0, board, potentialMoves);
                addValidEnd(r, c,  1, 0, board, potentialMoves);
            } else { 
                addValidEnd(r, c, 0, -1, board, potentialMoves);
                addValidEnd(r, c, 0,  1, board, potentialMoves);
            }
        } else {
            // pokud smer nezname, pridame vsechna 4 okolni policka
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int newR = r + d[0];
                int newC = c + d[1];
                if (Utils.isValid(newR, newC) && board[newR][newC] >= 0) {
                    potentialMoves.add(new int[]{newR, newC});
                }
            }
        }
        
        // zamichame poradi pro prvek nahody a pridame na zasobnik
        Collections.shuffle(potentialMoves);
        for (int[] move : potentialMoves) targets.push(move);
    }

    // hleda volne policko na konci rady zasahu
    private void addValidEnd(int r, int c, int dr, int dc, int[][] board, ArrayList<int[]> moves) {
        int currR = r + dr;
        int currC = c + dc;

        // preskocime jiz zasazene casti lode
        while (Utils.isValid(currR, currC) && board[currR][currC] == Utils.HIT) {
            currR += dr;
            currC += dc;
        }

        // pokud jsme nasli volne policko, pridame ho
        if (Utils.isValid(currR, currC) && board[currR][currC] >= 0) {
            moves.add(new int[]{currR, currC});
        }
    }
}