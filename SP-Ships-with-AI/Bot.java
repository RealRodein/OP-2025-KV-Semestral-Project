import java.util.Random;

public abstract class Bot {
    protected Random random = new Random();
    
    // Abstract method: Every bot must implement its own shooting logic
    // Returns int[] {row, col}
    public abstract int[] shoot(int[][] enemyBoard);
}