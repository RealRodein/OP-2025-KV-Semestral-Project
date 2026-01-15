public class Simple extends Bot {
    // nejjednodussi bot strilejici nahodne
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        return new int[]{random.nextInt(rows), random.nextInt(cols)};
    }
}