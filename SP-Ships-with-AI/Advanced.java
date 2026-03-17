package kin.op.kupec.vojtech;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public class Advanced extends Bot {
    // pokrocily bot s logikou pro dohledavani lodi
    // pouziva zasobnik pro ukladani potencialnich cilu
    private final Stack<Coordinates> targets = new Stack<>();
    private Coordinates lastShot = null;

    @Override
    public Coordinates shoot(Board enemyBoard) {
        int size = enemyBoard.getSize();

        // pokud byl predchozi tah uspesny, analyzujeme situaci
        if (lastShot != null) {
            int r = lastShot.x;
            int c = lastShot.y;

            if (enemyBoard.get(r, c) == Utils.HIT) {
                analyzeAndAddTargets(r, c, enemyBoard);
            }
        }

        // pokud mame cile v zasobniku, strilime na ne
        Coordinates shotCoords;
        while (!targets.isEmpty()) {
            shotCoords = targets.pop();
            // overime zda je cil stale platny (nepouzity)
            if (enemyBoard.get(shotCoords) >= 0) {
                lastShot = shotCoords;
                return shotCoords;
            }
        }

        // pokud nemame zadne prioritni cile, strilime nahodne
        int r, c;
        do {
            r = random.nextInt(size);
            c = random.nextInt(size);
        } while (enemyBoard.get(r, c) < 0);

        lastShot = new Coordinates(r, c);
        return lastShot;
    }

    // prida okolni policka zasazene lode do seznamu cilu
    private void analyzeAndAddTargets(int r, int c, Board board) {
        // detekce orientace lode podle sousednich zasahu
        boolean isVertical = (Utils.isValid(r - 1, c) && board.get(r - 1, c) == Utils.HIT) ||
                             (Utils.isValid(r + 1, c) && board.get(r + 1, c) == Utils.HIT);

        boolean isHorizontal = (Utils.isValid(r, c - 1) && board.get(r, c - 1) == Utils.HIT) ||
                               (Utils.isValid(r, c + 1) && board.get(r, c + 1) == Utils.HIT);

        ArrayList<Coordinates> potentialMoves = new ArrayList<>();

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
                if (Utils.isValid(newR, newC) && board.get(newR, newC) >= 0) {
                    potentialMoves.add(new Coordinates(newR, newC));
                }
            }
        }

        // zamichame poradi pro prvek nahody a pridame na zasobnik
        Collections.shuffle(potentialMoves);
        for (Coordinates move : potentialMoves) targets.push(move);
    }

    // hleda volne policko na konci rady zasahu
    private void addValidEnd(int r, int c, int dr, int dc, Board board, ArrayList<Coordinates> moves) {
        int currR = r + dr;
        int currC = c + dc;

        // preskocime jiz zasazene casti lode
        while (Utils.isValid(currR, currC) && board.get(currR, currC) == Utils.HIT) {
            currR += dr;
            currC += dc;
        }

        // pokud jsme nasli volne policko, pridame ho
        if (Utils.isValid(currR, currC) && board.get(currR, currC) >= 0) {
            moves.add(new Coordinates(currR, currC));
        }
    }
}