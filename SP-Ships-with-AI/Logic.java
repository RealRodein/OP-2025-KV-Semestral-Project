import java.util.List;
import java.util.Random;

public class Logic {
    // trida spravujici herni stav a logiku
    private int[][] playerBoard;
    private int[][] botBoard;
    // definice velikosti lodi
    private final int[] SHIPS = {5, 4, 3, 3, 2}; 
    private Random random = new Random();

    // promenne pro statistiku presnosti
    private int playerShots = 0;
    private int playerHits = 0;

    // inicializuje herni plochy a nahodne rozmisti lode
    public void initGame() {
        playerBoard = new int[Utils.BOARD_SIZE][Utils.BOARD_SIZE];
        botBoard = new int[Utils.BOARD_SIZE][Utils.BOARD_SIZE];
        placeShipsRandomly(botBoard);
        placeShipsRandomly(playerBoard); 
        playerShots = 0; 
        playerHits = 0; 
    }

    // zpracuje vystrel na konkretni souradnice
    // vraci: 0=vedle, 1=zasah, 2=potopeno, 3=opakovana, -1=chyba
    public int processShot(int[][] board, int r, int c, boolean isPlayerShooting) {
        if (!Utils.isValid(r, c)) {
            return -1; 
        }
        
        if (isPlayerShooting) {
            playerShots++;
        }

        int cell = board[r][c];

        // kontrola zda jiz nebylo na toto misto strileno
        if (cell == Utils.HIT || cell == Utils.MISS) {
            return 3;
        }

        // pokud je na souradnicich lod
        if (cell == Utils.SHIP) { 
            board[r][c] = Utils.HIT;
            if (isPlayerShooting) {
                playerHits++;
            }
            
            // kontrola zda tento zasah potopil celou lod
            if (isShipSunk(board, r, c)) {
                revealSurroundings(board, r, c);
                return 2; 
            }
            return 1; 
        } 
        
        // pokud na souradnicich nic neni
        board[r][c] = Utils.MISS;
        return 0; 
    }

    // overi zda jsou vsechny casti lode zasazeny
    private boolean isShipSunk(int[][] board, int r, int c) {
        List<int[]> parts = Utils.getShipParts(board, r, c);
        for (int[] p : parts) {
            // pokud najdeme cast lode ktera neni zasazena, lod neni potopena
            if (board[p[0]][p[1]] == Utils.SHIP) {
                return false;
            }
        }
        return true;
    }

    // automaticky odhali vodu okolo potopene lode
    private void revealSurroundings(int[][] board, int r, int c) {
        List<int[]> parts = Utils.getShipParts(board, r, c);
        for (int[] part : parts) {
            // pro kazdou cast lode projdeme okoli 3x3
            for (int ro = -1; ro <= 1; ro++) {
                for (int co = -1; co <= 1; co++) {
                    int nr = part[0] + ro;
                    int nc = part[1] + co;
                    // oznacime jako 'miss' jen pokud je to voda a jsme na desce
                    if (Utils.isValid(nr, nc) && board[nr][nc] == Utils.WATER) {
                        board[nr][nc] = Utils.MISS;
                    }
                }
            }
        }
    }

    // pokusi se nahodne rozmistit vsechny lode na desku
    private void placeShipsRandomly(int[][] board) {
        for (int shipSize : SHIPS) {
            boolean placed = false;
            int attempts = 0;
            // zkousime najit validni pozici (max 1000 pokusu)
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

    // overi zda je mozne lod umistit bez kolize s jinymi
    private boolean canPlaceShip(int[][] board, int row, int col, int size, boolean vertical) {
        // kontrola zda lod nepresahuje hraci plochu
        if (vertical) { 
            if (row + size > Utils.BOARD_SIZE) return false; 
        } else { 
            if (col + size > Utils.BOARD_SIZE) return false; 
        }

        // definice oblasti pro kontrolu kolizi (vcetne okoli)
        int rStart = Math.max(0, row - 1);
        int rEnd = Math.min(Utils.BOARD_SIZE, vertical ? row + size + 1 : row + 2);
        int cStart = Math.max(0, col - 1);
        int cEnd = Math.min(Utils.BOARD_SIZE, vertical ? col + 2 : col + size + 1);

        // kontrola zda v oblasti neni jina lod
        for (int r = rStart; r < rEnd; r++) {
            for (int c = cStart; c < cEnd; c++) {
                if (board[r][c] != Utils.WATER) {
                    return false; 
                }
            }
        }
        return true;
    }

    // fyzicky zapise lod do pole
    private void placeShip(int[][] board, int row, int col, int size, boolean vertical) {
        for (int i = 0; i < size; i++) {
            if (vertical) {
                board[row + i][col] = Utils.SHIP;
            } else {
                board[row][col + i] = Utils.SHIP;
            }
        }
    }
    
    // zkontroluje zda na desce zbyvaji nejake lode
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

    // pristupove metody pro zobrazeni stavu
    public int getPlayerShots() { return playerShots; }
    public int getPlayerHits() { return playerHits; }
    public int[][] getPlayerBoard() { return playerBoard; }
    public int[][] getBotBoard() { return botBoard; }
}