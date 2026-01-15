public class Standard extends Bot {
    // bot ktery nestrili na jiz zasazena mista
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        int r, c;
        
        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (enemyBoard[r][c] < 0); 

        return new int[]{r, c};
    }
}