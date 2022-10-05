package com.messik.v12.optimizer;

import java.util.Random;
import java.util.stream.IntStream;

public class Mutations {

    private static final Random RANDOM = new Random();

    public static int[] mutate(int[] values, int[][] ranges, double delta) {
        return IntStream.range(0, values.length)
                .map(i -> mutate(values[i], ranges[i], delta))
                .toArray();
    }

    public static int mutate(int value, int[] range, double delta) {
        if (RANDOM.nextBoolean()) {
            return value;
        }
        int min = range[0];
        int max = range[1];
        int mv = (int) ((max - min) * delta * 0.5);
        return Math.max(min, Math.min(max, value - mv + RANDOM.nextInt(1 + 2 * mv)));
    }

    public static int[] cross(int[] left, int[] right) {
        return IntStream.range(0, left.length)
                .map(i -> cross(left[i], right[i]))
                .toArray();
    }

    public static int cross(int left, int right) {
        return RANDOM.nextBoolean() ? left : right;
    }

    public static int[] generate(int[][] ranges) {
        return IntStream.range(0, ranges.length)
                .map(i -> generate(ranges[i]))
                .toArray();
    }

    public static int generate(int[] range) {
        int min = range[0];
        int max = range[1];
        return min + RANDOM.nextInt(max - min);
    }

    public static double[] mutate(double[] values, double[][] ranges, double delta) {
        return IntStream.range(0, values.length)
                .mapToDouble(i -> mutate(values[i], ranges[i], delta))
                .toArray();
    }

    public static double mutate(double value, double[] range, double delta) {
        double min = range[0];
        double max = range[1];
        double mv = (max - min) * delta * 0.5;
        return Math.max(min, Math.min(max, value - mv + RANDOM.nextDouble() * 2 * mv));
    }

    public static double[] generate(double[][] ranges) {
        return IntStream.range(0, ranges.length)
                .mapToDouble(i -> generate(ranges[i]))
                .toArray();
    }

    public static double generate(double[] range) {
        double min = range[0];
        double max = range[1];
        return min + RANDOM.nextDouble() * (max - min);
    }

    public static double[] cross(double[] left, double[] right) {
        return IntStream.range(0, left.length)
                .mapToDouble(i -> cross(left[i], right[i]))
                .toArray();
    }

    public static double cross(double left, double right) {
        return RANDOM.nextBoolean() ? left : right;
    }
}
