package kin.op.kupec.vojtech;

import java.util.Random;

public abstract class Bot {

    public Random random = new Random();

    // metoda pro vypocet tahu, kterou musi potomci implementovat
    public abstract Coordinates shoot(Board enemyBoard);
}