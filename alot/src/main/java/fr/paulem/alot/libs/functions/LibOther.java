package fr.paulem.alot.libs.functions;

import java.util.concurrent.ThreadLocalRandom;

public class LibOther {
    public int RandomBtw(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public double RandomBtw(double min, double max){
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }
}
