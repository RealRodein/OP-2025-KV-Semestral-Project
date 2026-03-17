package kin.op.kupec.vojtech;

public class Coordinates {
    // trida reprezentujici souradnice na hraci plose
    public final int x; // radek (row)
    public final int y; // sloupec (col)

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}