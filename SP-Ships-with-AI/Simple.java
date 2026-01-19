public class Simple extends Bot {
    // zakladni bot ktery strili zcela nahodne
    // ignoruje stav hraci plochy
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        return new int[]{random.nextInt(rows), random.nextInt(cols)};
    }
}