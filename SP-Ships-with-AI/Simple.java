package kin.op.kupec.vojtech;

public class Simple extends Bot {
    // zakladni bot ktery strili zcela nahodne
    // ignoruje stav hraci plochy
    @Override
    public Coordinates shoot(Board enemyBoard) {
        int size = enemyBoard.getSize();
        return new Coordinates(random.nextInt(size), random.nextInt(size));
    }
}