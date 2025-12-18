public class Standard extends Bot {
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        int r, c;
        
        // Keep generating until we find a spot that isn't hit(-1) or miss(-2)
        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (enemyBoard[r][c] < 0); 

        return new int[]{r, c};
    }
}