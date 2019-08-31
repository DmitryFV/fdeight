package com.fdeight.miscellaneous.simple.parts;

import java.util.Arrays;

public class Parts {

    private static final int[] EMPTY_RESULT = {};

    private final int[] values;

    private final int numParts;

    /**
     * Массив длиной такой же, как у {@link #values}.
     * Каждый элемент - номер части (нумерация с 0), в которую включен элемент с тем же индексом в {@link #values}.
     */
    private int[] results = null;

    /**
     * Вспомогательный массив для оптимизации по времени работы.
     * Получается выиграть немного (процентов 10), если не отводить память постоянно под этот массив,
     * а заполнять его нулями.
     */
    private int[] sum;


    Parts(final int[] values, final int numParts) {
        this.values = values;
        this.numParts = numParts;
    }

    int[] compute(final int[] values) {
        results = new int[values.length];
        Arrays.fill(results, -1);
        sum = new int[numParts];
        if (hasConditionsOfNoSolution()) {
            return EMPTY_RESULT;
        }
        if (computeElement(0)) {
            return results;
        } else {
            return EMPTY_RESULT;
        }
    }

    private boolean computeElement(final int startIndex) {
        if (startIndex == results.length) {
            return checkTerminalSum();
        } else {
            if (values[startIndex] == 0) {
                results[startIndex] = 0;
                return computeElement(startIndex + 1);
            }
            // Данная оптимизация (maxPartNumber) немного замедляет (скорее всего, за счет лишнего вычисления минимума)
            // в ситуациях, когда решение существует, но дает большой выигрыш (в разы), когда решения не существует
            // (можем отсекать часть вариантов, симметричных ранее проверенным вариантам).
            final int maxPartNumber = Math.min(startIndex, numParts - 1);
            for (int part = 0; part <= maxPartNumber; part++) {
                results[startIndex] = part;
                if (computeElement(startIndex + 1)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean checkTerminalSum() {
        Arrays.fill(sum, 0);
        for (int i = 0; i < values.length; i++) {
            sum[results[i]] += values[i];
        }
        for (int part = 0; part < numParts - 1; part++) {
            if (sum[part] != sum[part + 1]) {
                return false;
            }
        }
        return true;
    }

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

    private void output() {
        System.out.println("Values:");
        System.out.println(Arrays.toString(values));
        System.out.println("Results:");
        System.out.println(Arrays.toString(results));
    }

    public static void main(final String[] args) {
        final Parts parts = new Parts(new int[]{25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23},
                5);
//        final Parts parts = new Parts(new int[]{-1, -2, 3, 6, -4, 7, -9, 12, 5, -2, 1, 2, 0, 3, 4, 16, 0, 0, 8},
//                3);
        final long startTime = System.currentTimeMillis();
        parts.results = parts.compute(parts.values);
        System.out.println(String.format("Time %d ms", System.currentTimeMillis() - startTime));
        parts.output();
    }
}
