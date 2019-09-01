package com.fdeight.miscellaneous.simple.parts;

import java.util.Arrays;

public class PartsDynamic extends Parts {
    static class PartsDynamicFactory extends PartsFactory {
        @Override
        Parts createParts(final int[] values, final int numParts) {
            return new PartsDynamic(values, numParts);
        }
    }

    static final PartsFactory factory = new PartsDynamicFactory();

    private static final int NO_SOLUTION = -1;

    /**
     * Первый индекс: сумма чисел, не включенных в часть 0.
     * Второй индекс: номер текущего размещаемого элемента.
     * Значение - номер части, в которую размещен элемент,
     * либо {@link #NOT_COMPUTED}, если вычисления еще не производились,
     * либо {@link #NO_SOLUTION}, если было определено, что решения нет.
     */
    private int[][] partsStorage;

    private PartsDynamic(final int[] values, final int numParts) {
        super(values, numParts);
        if (numParts != 2) {
            throw new IllegalArgumentException(String.format("numParts (%d) != 2", numParts));
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] < 0) {
                throw new IllegalArgumentException(String.format("values[%d] (%d) < 0", i, values[i]));
            }
        }
    }

    @Override
    protected boolean computeAlgorithm() {
        final int sumValues = computeSumValues();
        final int sumPart = sumValues / numParts;
        partsStorage = new int[sumPart + 1][values.length];
        for (final int[] movesLine : partsStorage) {
            Arrays.fill(movesLine, NOT_COMPUTED);
        }
        return computeState(sumPart, values.length - 1);
    }

    private boolean computeState(final int sumPart, final int indexValue) {
        if (indexValue == -1) {
            return sumPart == 0;
        }
        final boolean isAvailable = sumPart - values[indexValue] >= 0;
        if (isAvailable) {
            if (partsStorage[sumPart - values[indexValue]][indexValue] == 0) {
                results[indexValue] = 0;
            } else if (partsStorage[sumPart - values[indexValue]][indexValue] == NOT_COMPUTED) {
                if (computeState(sumPart - values[indexValue], indexValue - 1)) {
                    setPart(sumPart - values[indexValue], indexValue, 0);
                } else {
                    setPart(sumPart - values[indexValue], indexValue, NO_SOLUTION);
                }
            }
        }
        if (results[indexValue] == NOT_COMPUTED || results[indexValue] == NO_SOLUTION) {
            if (partsStorage[sumPart][indexValue] == 1) {
                results[indexValue] = 1;
            } else if (partsStorage[sumPart][indexValue] == NOT_COMPUTED) {
                if (computeState(sumPart, indexValue - 1)) {
                    setPart(sumPart, indexValue, 1);
                } else {
                    setPart(sumPart, indexValue, NO_SOLUTION);
                }
            }
        }
        return results[indexValue] != NO_SOLUTION;
    }

    private void setPart(final int sumPart, final int indexValue, final int part) {
        results[indexValue] = part;
        partsStorage[sumPart][indexValue] = part;
    }
}
