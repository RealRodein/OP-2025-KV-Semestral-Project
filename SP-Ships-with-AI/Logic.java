import java.util.List;
import java.util.Random;

public class Logic {
    // trida zajistujici herni logiku
    private int[][] playerBoard;
    private int[][] botBoard;
    private final int[] SHIPS = {5, 4, 3, 3, 2}; 
    private Random random = new Random();

    // promenne pro statistiku
    private int playerShots = 0;
    private int playerHits = 0;

    // inicializuje herni plochy a lode
    public void initGame() {
        playerBoard = new int[Utils.BOARD_SIZE][Utils.BOARD_SIZE];
        botBoard = new int[Utils.BOARD_SIZE][Utils.BOARD_SIZE];
        placeShipsRandomly(botBoard);
        placeShipsRandomly(playerBoard); 
        playerShots = 0; 
        playerHits = 0; 
    }

    // zpracuje vystrel a vrati vysledek
    public int processShot(int[][] board, int r, int c, boolean isPlayerShooting) {
        if (!Utils.isValid(r, c)) {
            return -1; 
        }
        
        if (isPlayerShooting) {
            playerShots++;
        }

        int cell = board[r][c];

        if (cell == Utils.HIT || cell == Utils.MISS) {
            return 3;
        }

        if (cell == Utils.SHIP) { 
            board[r][c] = Utils.HIT;
            if (isPlayerShooting) {
                playerHits++;
            }
            
            if (isShipSunk(board, r, c)) {
                revealSurroundings(board, r, c);
                return 2; 
            }
            return 1; 
        } 
        
        board[r][c] = Utils.MISS;
        return 0; 
    }

    // zkontroluje zda je lod potopena
    private boolean isShipSunk(int[][] board, int r, int c) {
        List<int[]> parts = Utils.getShipParts(board, r, c);
        for (int[] p : parts) {
            if (board[p[0]][p[1]] == Utils.SHIP) {
                return false;
            }
        }
        return true;
    }

    // odhali policka okolo potopene lode
    private void revealSurroundings(int[][] board, int r, int c) {
        List<int[]> parts = Utils.getShipParts(board, r, c);
        for (int[] part : parts) {
            for (int ro = -1; ro <= 1; ro++) {
                for (int co = -1; co <= 1; co++) {
                    int nr = part[0] + ro;
                    int nc = part[1] + co;
                    if (Utils.isValid(nr, nc) && board[nr][nc] == Utils.WATER) {
                        board[nr][nc] = Utils.MISS;
                    }
                }
            }
        }
    }

    // nahodne rozmisti lode na desku
    private void placeShipsRandomly(int[][] board) {
        for (int shipSize : SHIPS) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 1000) {
                int row = random.nextInt(Utils.BOARD_SIZE);
                int col = random.nextInt(Utils.BOARD_SIZE);
                boolean vertical = random.nextBoolean();

                if (canPlaceShip(board, row, col, shipSize, vertical)) {
                    placeShip(board, row, col, shipSize, vertical);
                    placed = true;
                }
                attempts++;
            }
        }
    }

    // overi zda se lod vejde na dane misto
    private boolean canPlaceShip(int[][] board, int row, int col, int size, boolean vertical) {
        if (vertical) { 
            if (row + size > Utils.BOARD_SIZE) return false; 
        } else { 
            if (col + size > Utils.BOARD_SIZE) return false; 
        }

        int rStart = Math.max(0, row - 1);
        int rEnd = Math.min(Utils.BOARD_SIZE, vertical ? row + size + 1 : row + 2);
        int cStart = Math.max(0, col - 1);
        int cEnd = Math.min(Utils.BOARD_SIZE, vertical ? col + 2 : col + size + 1);

        for (int r = rStart; r < rEnd; r++) {
            for (int c = cStart; c < cEnd; c++) {
                if (board[r][c] != Utils.WATER) {
                    return false; 
                }
            }
        }
        return true;
    }

    // zapise lod do pole
    private void placeShip(int[][] board, int row, int col, int size, boolean vertical) {
        for (int i = 0; i < size; i++) {
            if (vertical) {
                board[row + i][col] = Utils.SHIP;
            } else {
                board[row][col + i] = Utils.SHIP;
            }
        }
    }
    
    // overi podminku vitezstvi
    public boolean checkWin(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == Utils.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    // gettery
    public int getPlayerShots() { return playerShots; }
    public int getPlayerHits() { return playerHits; }
    public int[][] getPlayerBoard() { return playerBoard; }
    public int[][] getBotBoard() { return botBoard; }
}