package com.fdeight.miscellaneous.simple.parts;

import java.util.Arrays;

public class PartsFull extends Parts {
    static class PartsFullFactory extends PartsFactory {
        @Override
        Parts createParts(final int[] values, final int numParts) {
            return new PartsFull(values, numParts);
        }
    }

    static final PartsFactory factory = new PartsFullFactory();

    /**
     * Вспомогательный массив для оптимизации по времени работы.
     * Получается выиграть немного (процентов 10), если не отводить память постоянно под этот массив,
     * а заполнять его нулями.
     */
    private int[] sum;

    private PartsFull(final int[] values, final int numParts) {
        super(values, numParts);
    }

    @Override
    protected boolean computeAlgorithm() {
        sum = new int[numParts];
        return computeElement(0);
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
}
