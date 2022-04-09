package com.fdeight.sport.utils;

import java.util.function.Supplier;

public class Utils {
    public static void checkEquals(final int value1, final int value2, final Supplier<String> labelSupplier) {
        if (value1 == value2) return;
        throw new IllegalStateException(String.format("%s: %d != %d", labelSupplier.get(), value1, value2));
    }

    public static void checkNotEquals(final int value1, final int value2, final Supplier<String> labelSupplier) {
        if (value1 != value2) return;
        throw new IllegalStateException(String.format("%s: %d == %d", labelSupplier.get(), value1, value2));
    }

    public static void checkNotNegative(final int value, final Supplier<String> labelSupplier) {
        if (value >= 0) return;
        throw new IllegalStateException(String.format("%s: %d < 0", labelSupplier.get(), value));
    }

    public static void checkInterval(final int value, final int min, final int max,
                                     final Supplier<String> labelSupplier) {
        if (min <= value && value <= max) return;
        throw new IllegalStateException(String.format("%s: %d is not in [%d,%d]",
                labelSupplier.get(), value, min, max));
    }

    public static void impossibleIllegalState() {
        throw new IllegalStateException("Impossible illegal state");
    }
}