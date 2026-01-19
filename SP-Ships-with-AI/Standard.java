public class Standard extends Bot {
    // bot stredni urovne, ktery si pamatuje kam strilil
    // nestrili dvakrat na stejne misto
    @Override
    public int[] shoot(int[][] enemyBoard) {
        int rows = enemyBoard.length;
        int cols = enemyBoard[0].length;
        int r, c;
        
        // generuje souradnice dokud nenajde volne policko
        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (enemyBoard[r][c] < 0); 

        return new int[]{r, c};
    }
}