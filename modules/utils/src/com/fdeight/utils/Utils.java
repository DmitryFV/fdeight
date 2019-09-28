package com.fdeight.utils;

public class Utils {

    public static void checkArgument(final int value, final int bound1, final int bound2, final String label) {
        if (value < Math.min(bound1, bound2) || value > Math.max(bound1, bound2)) {
            throw new IllegalArgumentException(String.format("%s: value (%d) not in [%d;%d]",
                    label, value, Math.min(bound1, bound2), Math.max(bound1, bound2)));
        }
    }
}
