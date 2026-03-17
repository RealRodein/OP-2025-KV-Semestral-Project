package kin.op.kupec.vojtech;

public class Board {
    // trida reprezentujici hraci plochu
    private final int[][] grid;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.grid = new int[size][size];
    }

    // vrati hodnotu policka na zadanych souradnicich
    public int get(int r, int c) {
        return grid[r][c];
    }

    public int get(Coordinates coords) {
        return grid[coords.x][coords.y];
    }

    // nastavi hodnotu policka na zadanych souradnicich
    public void set(int r, int c, int value) {
        grid[r][c] = value;
    }

    public void set(Coordinates coords, int value) {
        grid[coords.x][coords.y] = value;
    }

    // vrati velikost (rozmer) herni plochy
    public int getSize() {
        return size;
    }

    // vrati interni pole pro zpetnou kompatibilitu s metodami ktere ho potrebuji
    public int[][] getGrid() {
        return grid;
    }

    // zkontroluje zda na desce zbyvaji nejake lode
    public boolean hasShipsRemaining() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == Utils.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }
}