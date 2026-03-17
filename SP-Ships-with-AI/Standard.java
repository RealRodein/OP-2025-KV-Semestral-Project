package kin.op.kupec.vojtech;

public class Standard extends Bot {
    // bot stredni urovne, ktery si pamatuje kam strilel
    // nestrili dvakrat na stejne misto
    @Override
    public Coordinates shoot(Board enemyBoard) {
        int size = enemyBoard.getSize();
        int r, c;

        // generuje souradnice dokud nenajde volne policko
        do {
            r = random.nextInt(size);
            c = random.nextInt(size);
        } while (enemyBoard.get(r, c) < 0);

        return new Coordinates(r, c);
    }
}