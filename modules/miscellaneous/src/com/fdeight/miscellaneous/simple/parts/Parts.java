package com.fdeight.miscellaneous.simple.parts;

import java.util.Arrays;

public abstract class Parts {
    static abstract class PartsFactory {
        abstract Parts createParts(final int[] values, final int numParts);
    }

    private static final int[] EMPTY_RESULT = {};

    static final int NOT_COMPUTED = -2;

    final int[] values;

    final int numParts;

    /**
     * Массив длиной такой же, как у {@link #values}.
     * Каждый элемент - номер части (нумерация с 0), в которую включен элемент с тем же индексом в {@link #values}.
     */
    int[] results = null;

    Parts(final int[] values, final int numParts) {
        this.values = values;
        this.numParts = numParts;
    }

    int[] compute() {
        if (hasConditionsOfNoSolution()) {
            return EMPTY_RESULT;
        }
        results = new int[values.length];
        Arrays.fill(results, NOT_COMPUTED);
        if (computeAlgorithm()) {
            checkResults();
            return results;
        } else {
            return EMPTY_RESULT;
        }
    }

    /**
     * @return {@code true}, если решение найдено.
     */
    protected abstract boolean computeAlgorithm();

    private boolean hasConditionsOfNoSolution() {
        int sumValues = 0;
        boolean hasNegative = false;
        for (final int value : values) {
            sumValues += value;
            if (value < 0) {
                hasNegative = true;
            }
        }
        if (sumValues % numParts != 0) {
            return true;
        }
        if (hasNegative) {
            return false;
        }
        final int sumPart = sumValues / numParts;
        for (final int value : values) {
            if (value > sumPart) {
                return true;
            }
        }
        return false;
    }

    private void checkResults() {
        final int[] sum = new int[numParts];
        for (int i = 0; i < values.length; i++) {
            sum[results[i]] += values[i];
        }
        for (int part = 0; part < numParts - 1; part++) {
            if (sum[part] != sum[part + 1]) {
                throw new IllegalStateException(String.format("sum[%d] (%d) != sum[%d] (%d)",
                        part, sum[part], part + 1, sum[part + 1]));
            }
        }
    }

    private void output() {
        System.out.println(String.format("numParts: %d, length: %d, values:", numParts, values.length));
        System.out.println(Arrays.toString(values));
        final int sumValues = computeSumValues();
        final int sumPart = sumValues / numParts;
        System.out.println(String.format("Sum: %d, sumPart: %d", sumValues, sumPart));
        System.out.println("Results:");
        System.out.println(Arrays.toString(results));
    }

    int computeSumValues() {
        int sumValues = 0;
        for (final int value : values) {
            sumValues += value;
        }
        return sumValues;
    }

    public static void main(final String[] args) {
        final Parts parts = PartsFull.factory.createParts(
                new int[]{25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23},
                5);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{-1, -2, 3, 6, -4, 7, -9, 12, 5, -2, 1, 2, 0, 3, 4, 16, 0, 0, 8},
//                3);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{-2, -1, -3, -8, -5, -1, -4, -6, -3, -6, -10, -2, -2, -2},
//                5);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{-1, -1, 2, 6, 3, -1, 2, 6, 3, -1, 2, 3, 4, 2, 3, 4},
//                2);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
//                        25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
//                        25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
//                        25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
//                },
//                2);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{10750, 99, 4, 4, 1, 2, 3, 4, 10, 98, 10750, 1},
//                2);
//        final Parts parts = PartsDynamic.factory.createParts(
//                new int[]{10750, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 12, 3, 23,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 32, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        25, 1, 24, 10, 10, 1, 2, 3, 4, 10, 5, 25, 50, 1, 2, 52, 3, 24,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 12, 10, 1, 2, 3, 4, 16, 5, 25, 50, 1, 2, 62, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        55, 1, 24, 10, 11, 1, 2, 3, 4, 16, 5, 75, 50, 1, 2, 72, 3, 23,
//                        10750, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1,
//                        10750, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1,
//                        10759, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1, 5000,
//                        10759, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1, 5000,
//                },
//                2);
        final long startTime = System.currentTimeMillis();
        parts.results = parts.compute();
        System.out.println(String.format("Time %d ms", System.currentTimeMillis() - startTime));
        parts.output();
    }
}
