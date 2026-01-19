import java.util.Random;

public abstract class Bot {
    // abstraktni rodicovska trida pro vsechny boty
    protected Random random = new Random();
    
    // metoda pro vypocet tahu, kterou musi potomci implementovat
    public abstract int[] shoot(int[][] enemyBoard);
}