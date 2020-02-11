package com.openkappa.runtime.stringsearch.generators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.SplittableRandom;

public class MarkovChainDataGenerator implements DataGenerator {

    public static MarkovChainDataGenerator from(Path file, long seed) throws IOException {
        double[][] frequency = new double[256][256];
        double[] distribution = new double[256];
        try (var reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                frequency[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = Integer.parseInt(split[2]);
            }
        }
        double total = 0D;
        for (int i = 0; i < frequency.length; ++i) {
            double sum = sum(frequency[i]);
            distribution[i] = sum;
            total += sum;
            if (sum > 0) {
                for (int j = 0; j < frequency[i].length; ++j) {
                    frequency[i][j] /= sum;
                }
            }
        }
        SplittableRandom random = new SplittableRandom(seed);
        X[] holders = new X[256];
        Arrays.setAll(holders, i -> new X());
        Distribution[] conditionals = new Distribution[256];
        byte[] order = new byte[256];
        for (int i = 0; i < distribution.length; ++i) {
            distribution[i] /= total;
            if (distribution[i] > 0) {
                int size = computeCDF(holders, frequency[i], order);
                conditionals[i] = new Distribution(random, Arrays.copyOf(order, size), Arrays.copyOf(frequency[i], size));
            }
        }
        int size = computeCDF(holders, distribution, order);
        int position = Arrays.binarySearch(distribution, 0, size, random.nextDouble());

        return new MarkovChainDataGenerator(order[position >= 0 ? position : Math.min(size, -position - 1)], conditionals);
    }

    private final Distribution[] conditionals;

    private byte next;

    public MarkovChainDataGenerator(byte first, Distribution[] conditionals) {
        this.conditionals = conditionals;
        this.next = first;
    }


    @Override
    public byte nextByte() {
        byte current = next;
        var conditional = conditionals[next & 0xFF];
        next = conditional.nextByte();
        return current;
    }

    private static class Distribution {

        private final SplittableRandom random;

        private final byte[] bytes;
        private final double[] cdf;

        private Distribution(SplittableRandom random, byte[] bytes, double[] cdf) {
            this.random = random;
            this.bytes = bytes;
            this.cdf = cdf;
        }

        public byte nextByte() {
            double r = random.nextDouble();
            int index = Arrays.binarySearch(cdf, r);
            return bytes[index >= 0 ? index : Math.min(bytes.length, -index - 1)];
        }

    }

    private static double sum(double[] data) {
        double sum = 0;
        for (double datum : data) {
            sum += datum;
        }
        return sum;
    }

    private static int computeCDF(X[] holders, double[] frequencies, byte[] order) {
        int count = 0;
        for (int i = 0; i < frequencies.length; ++i) {
            var holder = holders[i];
            holder.value = (byte)i;
            holder.frequency = frequencies[i];
            count += (frequencies[i] > 0) ? 1 : 0;
        }
        Arrays.sort(holders);
        Arrays.fill(frequencies, 0);
        Arrays.fill(order, (byte)0);
        double cumsum = 0;

        for (int i = 0; i < count; ++i) {
            var x = holders[i];
            cumsum += x.frequency;
            frequencies[i] = cumsum;
            order[i] = x.value;
        }
        return count;
    }

    private static class X implements Comparable<X> {
        byte value;
        double frequency;

        @Override
        public int compareTo(X x) {
            return Double.compare(x.frequency, frequency);
        }
    }
}
