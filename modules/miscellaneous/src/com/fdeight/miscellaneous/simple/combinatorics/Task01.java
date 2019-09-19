package com.fdeight.miscellaneous.simple.combinatorics;

import java.util.HashSet;
import java.util.Set;

/**
 * Даны символы A, B, C, 1, 2, 3, 4.
 * 1. Сколько существует четырехзначных кодов из указанных цифр (повторы символов разрешены).
 * 2. Сколько существует четырехзначных кодов из указанных цифр (без повторов символов).
 */
public class Task01 {

    private static long factorial(final int n) {
        if (n == 0) {
            return 1;
        } else if (n < 0) {
            throw new IllegalArgumentException("n < 0");
        }
        long n1 = 1;
        long result = 1;
        for (int i = 0; i < n; i++) {
            result *= n1;
            n1++;
        }
        return result;
    }

    static long withDublicates(final int count, final int numSymbols) {
        return (long) Math.pow(numSymbols, count);
    }

    static long withDublicatesAll(final int count, final int numSymbols) {
        return countAllWith(count, numSymbols, 0, 0);
    }

    private static long countAllWith(final int count, final int numSymbols, final int level, final long result) {
        if (level == count) {
            return 1;
        }
        long newResult = result;
        for (int i = 0; i < numSymbols; i++) {
            newResult += countAllWith(count, numSymbols, level + 1, result);
        }
        return newResult;
    }

    static long withoutDublicates(final int count, final int numSymbols) {
        return factorial(numSymbols) / factorial(numSymbols - count);
    }

    static long withoutDublicatesAll(final int count, final int numSymbols) {
        return countAllWithout(count, numSymbols, 0, 0, new HashSet<>());
    }

    private static long countAllWithout(final int count, final int numSymbols, final int level, final long result,
                                        final Set<Integer> set) {
        if (level == count) {
            return 1;
        }
        long newResult = result;
        for (int i = 0; i < numSymbols; i++) {
            if (set.contains(i)) {
                continue;
            }
            set.add(i);
            newResult += countAllWithout(count, numSymbols, level + 1, result, set);
            set.remove(i);
        }
        return newResult;
    }

    public static void main(final String[] args) {
        System.out.println(factorial(4));
        System.out.println(withDublicates(6, 9));
        System.out.println(withDublicatesAll(6, 9));
        System.out.println(withoutDublicates(7, 12));
        System.out.println(withoutDublicatesAll(7, 12));
    }
}
