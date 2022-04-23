package com.fdeight.sport.utils;

import java.util.Objects;
import java.util.function.Supplier;

public class Utils {
    public static void checkEquals(final long value1, final long value2, final Supplier<String> labelSupplier) {
        if (value1 == value2) return;
        throw new IllegalStateException(String.format("%s: %d != %d", labelSupplier.get(), value1, value2));
    }

    public static void checkEquals(final double value1, final double value2, final Supplier<String> labelSupplier) {
        if (Math.abs(value1 - value2) < 1e-12) return;
        throw new IllegalStateException(String.format("%s: %5.3f != %5.3f", labelSupplier.get(), value1, value2));
    }

    public static void checkEquals(final String value1, final String value2, final Supplier<String> labelSupplier) {
        if (Objects.requireNonNull(value1, labelSupplier.get()).equals(value2)) return;
        throw new IllegalStateException(String.format("%s: %s != %s", labelSupplier.get(), value1, value2));
    }

    public static void checkNotEquals(final int value1, final int value2, final Supplier<String> labelSupplier) {
        if (value1 != value2) return;
        throw new IllegalStateException(String.format("%s: %d == %d", labelSupplier.get(), value1, value2));
    }

    public static void checkNotNegative(final int value, final Supplier<String> labelSupplier) {
        if (value >= 0) return;
        throw new IllegalStateException(String.format("%s: %d < 0", labelSupplier.get(), value));
    }

    public static void checkNotNegative(final double value, final Supplier<String> labelSupplier) {
        if (value >= 0) return;
        throw new IllegalStateException(String.format("%s: %5.3f < 0", labelSupplier.get(), value));
    }

    public static void checkInterval(final int value, final int min, final int max,
                                     final Supplier<String> labelSupplier) {
        if (min <= value && value <= max) return;
        throw new IllegalStateException(String.format("%s: %d is not in [%d,%d]",
                labelSupplier.get(), value, min, max));
    }

    public static void impossibleIllegalState(final String description) {
        throw new IllegalStateException(String.format("Impossible illegal state, %s", description));
    }

    public static int round(final double value) {
        return (int) Math.round(value);
    }
}
