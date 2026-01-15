import java.util.Random;

public abstract class Bot {
    // abstraktni trida pro bota
    protected Random random = new Random();
    
    // metoda kterou musi potomci implementovat
    public abstract int[] shoot(int[][] enemyBoard);
}