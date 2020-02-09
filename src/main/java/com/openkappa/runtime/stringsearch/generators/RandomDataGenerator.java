package com.openkappa.runtime.stringsearch.generators;

import java.util.SplittableRandom;

public class RandomDataGenerator implements DataGenerator {

    private final SplittableRandom random;
    private final int min;
    private final int max;

    public RandomDataGenerator(long seed) {
        this(seed, (byte)0, (byte)255);
    }

    public RandomDataGenerator(long seed, byte min, byte max) {
        this.random = new SplittableRandom(seed);
        this.min = min & 0xFF;
        this.max = max & 0xFF;
    }

    @Override
    public byte nextByte() {
        return (byte)random.nextInt(min, max);
    }

}
