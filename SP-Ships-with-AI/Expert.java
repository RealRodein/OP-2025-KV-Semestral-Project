import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Expert extends Bot {
    private final int[] FLEET = {5, 4, 3, 3, 2};

    @Override
    public int[] shoot(int[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        int[][] heatmap = new int[rows][cols];

        List<Integer> sunkSizes = new ArrayList<>();
        List<int[]> activeHits = new ArrayList<>();
        boolean[][] visited = new boolean[rows][cols];

        // 1. Analyze the board state
        // We separate hits into "Sunk" (revealed) and "Active" (burning)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == Utils.HIT && !visited[r][c]) {
                    List<int[]> component = Utils.getShipParts(board, r, c);
                    for (int[] p : component) visited[p[0]][p[1]] = true;
                    
                    if (isSunk(board, component)) {
                        sunkSizes.add(component.size());
                    } else {
                        activeHits.addAll(component);
                    }
                }
            }
        }

        // 2. Determine which ships are still alive
        List<Integer> remainingShips = new ArrayList<>();
        for (int size : FLEET) remainingShips.add(size);
        sunkSizes.sort(Collections.reverseOrder());
        // Remove sunk ships from our tracking (largest first to match correctly)
        for (int sunk : sunkSizes) remainingShips.remove((Integer) sunk);

        // 3. Calculate Strategy
        // If we have active hits, we are in TARGET mode (Focus fire).
        // If not, we are in HUNT mode (Search pattern).
        boolean targetMode = !activeHits.isEmpty();
        
        int minShipSize = 2;
        if (!remainingShips.isEmpty()) {
            minShipSize = Collections.min(remainingShips);
        }

        for (int size : remainingShips) {
            // In Target Mode, we only care about placements that overlap our active hits
            addProbabilities(board, heatmap, size, targetMode, activeHits);
        }

        // 4. Optimization: Parity (Checkerboard) Logic
        // In Hunt Mode, we only need to shoot every X cells, where X is the smallest ship.
        // Standard Battleship parity is 2 (black/white squares).
        if (!targetMode) {
            applyParityMask(heatmap, minShipSize);
        }

        return getBestShot(heatmap, board);
    }

    /**
     * Determines if a component is sunk based on the Game Logic rule:
     * When a ship sinks, its surroundings are revealed (turned to MISS).
     * Therefore, a ship is sunk if it has NO Water neighbors.
     */
    private boolean isSunk(int[][] board, List<int[]> component) {
        for (int[] part : component) {
            int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
            for (int[] d : dirs) {
                int nr = part[0] + d[0];
                int nc = part[1] + d[1];
                // If we see WATER next to a hit, the ship is definitely NOT sunk yet.
                if (Utils.isValid(nr, nc) && board[nr][nc] == Utils.WATER) return false;
            }
        }
        return true;
    }
    
    /**
     * Calculates the Probability Density Function (Heatmap).
     */
    private void addProbabilities(int[][] board, int[][] map, int size, boolean targetMode, List<int[]> hits) {
        int sizeBoard = board.length;
        
        // 0 = Horizontal, 1 = Vertical
        for (int axis = 0; axis < 2; axis++) { 
            boolean vertical = (axis == 1);
            int rLimit = vertical ? sizeBoard - size : sizeBoard;
            int cLimit = vertical ? sizeBoard : sizeBoard - size;

            for (int r = 0; r <= rLimit; r++) { // Fixed off-by-one error (should be <= rLimit inside loops if calculating strictly)
                                                // Actually standard loop is r < sizeBoard, check boundaries inside or limit loop. 
                                                // Using simple boundary limits:
                for (int c = 0; c <= cLimit; c++) {
                    
                    if (canFit(board, r, c, size, vertical)) {
                        boolean intersectsActiveHit = intersects(r, c, size, vertical, hits);
                        
                        // LOGIC BRANCH:
                        // 1. If in Target Mode: ONLY add weight if this placement passes through an existing hit.
                        // 2. If in Hunt Mode: Add weight everywhere.
                        if (targetMode) {
                            if (intersectsActiveHit) {
                                // Heavily weight placements that align with multiple hits
                                int alignmentBonus = getAlignmentBonus(r, c, size, vertical, hits);
                                increment(map, r, c, size, vertical, 10 + alignmentBonus);
                            }
                        } else {
                            increment(map, r, c, size, vertical, 1);
                        }
                    }
                }
            }
        }
    }

    private boolean canFit(int[][] board, int r, int c, int size, boolean vert) {
        for (int i = 0; i < size; i++) {
            int cr = vert ? r + i : r;
            int cc = vert ? c : c + i;
            if (!Utils.isValid(cr, cc)) return false; 
            int cell = board[cr][cc];
            // We can place a ship on WATER or an existing HIT (if we are guessing it's part of this ship)
            // We CANNOT place it on a MISS.
            if (cell == Utils.MISS) return false;
        }
        return true;
    }

    private boolean intersects(int r, int c, int size, boolean vert, List<int[]> hits) {
        if (hits.isEmpty()) return false;
        for (int i = 0; i < size; i++) {
            int cr = vert ? r + i : r;
            int cc = vert ? c : c + i;
            for (int[] h : hits) {
                if (h[0] == cr && h[1] == cc) return true;
            }
        }
        return false;
    }
    
    /**
     * If existing hits are horizontal, a vertical placement through them is unlikely 
     * (unless ships are touching, but we prioritize the single ship first).
     * This adds weight if the proposed ship covers MORE than 1 existing hit.
     */
    private int getAlignmentBonus(int r, int c, int size, boolean vert, List<int[]> hits) {
        int overlapCount = 0;
        for (int i = 0; i < size; i++) {
            int cr = vert ? r + i : r;
            int cc = vert ? c : c + i;
            for (int[] h : hits) {
                if (h[0] == cr && h[1] == cc) overlapCount++;
            }
        }
        // If we cover 2+ hits, this is a VERY probable placement. Boost it.
        return (overlapCount > 1) ? 100 : 0; 
    }

    private void increment(int[][] map, int r, int c, int size, boolean vert, int weight) {
        for (int i = 0; i < size; i++) {
            int cr = vert ? r + i : r;
            int cc = vert ? c : c + i;
            map[cr][cc] += weight;
        }
    }

    /**
     * Applies a "Parity" mask.
     * We only need to hunt on a checkerboard pattern. 
     * If (row + col) is even (or odd), we shoot. We skip the others.
     * This mathematically guarantees finding ships of size >= 2 while shooting 50% less.
     */
    private void applyParityMask(int[][] map, int minShipSize) {
        // We can usually stick to parity 2 for efficiency.
        // Higher parities (like 3) are risky if there's a size 2 ship left.
        int parity = 2; 
        
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                if ((r + c) % parity != 0) {
                    map[r][c] = 0; // Zero out probabilities on "white" squares
                }
            }
        }
    }

    private int[] getBestShot(int[][] map, int[][] board) {
        int max = -1;
        List<int[]> candidates = new ArrayList<>();

        // Find the maximum probability score
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                // Must be a legal shot (Unknown/Water) or a hidden ship (which looks like Water/Ship to logic but Water to us)
                // In this bot logic, we check board directly. 
                // We shouldn't shoot at HIT (-1) or MISS (-2).
                if (board[r][c] >= 0) { 
                    if (map[r][c] > max) {
                        max = map[r][c];
                        candidates.clear();
                        candidates.add(new int[]{r, c});
                    } else if (map[r][c] == max) {
                        candidates.add(new int[]{r, c});
                    }
                }
            }
        }
        
        // Pick a RANDOM candidate from the best ones to avoid top-left bias
        if (!candidates.isEmpty()) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        // Fallback (Should rarely happen unless board is full)
        int[] fallback = new int[2];
        do {
            fallback[0] = random.nextInt(board.length);
            fallback[1] = random.nextInt(board.length);
        } while (board[fallback[0]][fallback[1]] < 0);
        return fallback;
    }
}