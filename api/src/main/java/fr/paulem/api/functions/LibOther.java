package fr.paulem.api.functions;

import java.util.concurrent.ThreadLocalRandom;

public class LibOther {
    public static int RandomBtw(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double RandomBtw(double min, double max){
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }
}
