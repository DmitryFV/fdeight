package com.fdeight.miscellaneous.simple.parts;

import java.util.Arrays;

public abstract class Parts {
    static abstract class PartsFactory {
        abstract Parts createParts(final int[] values, final int numParts);
    }

    static final int[] EMPTY_RESULT = {};

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

    abstract int[] compute(final int[] values);

    boolean hasConditionsOfNoSolution() {
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

    private void output() {
        System.out.println("Values:");
        System.out.println(Arrays.toString(values));
        System.out.println("Results:");
        System.out.println(Arrays.toString(results));
    }

    public static void main(final String[] args) {
        final Parts parts = PartsFull.factory.createParts(
                new int[]{25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23},
                5);
//        final Parts parts = PartsFull.factory.createParts(
//                new int[]{-1, -2, 3, 6, -4, 7, -9, 12, 5, -2, 1, 2, 0, 3, 4, 16, 0, 0, 8},
//                3);
        final long startTime = System.currentTimeMillis();
        parts.results = parts.compute(parts.values);
        System.out.println(String.format("Time %d ms", System.currentTimeMillis() - startTime));
        parts.output();
    }
}
