public class Simple extends Bot {
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        // Pure random, no logic checking
        return new int[]{random.nextInt(rows), random.nextInt(cols)};
    }
}