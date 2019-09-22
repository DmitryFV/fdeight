package com.fdeight.miscellaneous.simple.combinatorics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Task01Test {

    @Test
    void testWithDuplicates() {
        final int count = 7;
        final int numSymbols = 10;
        final long value1 = Task01.withDuplicates(count, numSymbols);
        final long value2 = Task01.withDuplicatesAll(count, numSymbols);
        assertEquals(value1, value2);
    }

    @Test
    void testWithDuplicates01() {
        testWithDuplicates(4, 7, 2401);
    }

    @Test
    void testWithDuplicates02() {
        testWithDuplicates(3, 8, 512);
    }

    void testWithDuplicates(final int count, final int numSymbols, final long expected) {
        final long value = Task01.withDuplicates(count, numSymbols);
        assertEquals(expected, value);
    }

    @Test
    void testWithoutDuplicates() {
        final int count = 6;
        final int numSymbols = 10;
        final long value1 = Task01.withoutDuplicates(count, numSymbols);
        final long value2 = Task01.withoutDuplicatesAll(count, numSymbols);
        assertEquals(value1, value2);
    }

    @Test
    void testWithoutDuplicates01() {
        testWithoutDuplicates(4, 7, 840);
    }

    @Test
    void testWithoutDuplicates02() {
        testWithoutDuplicates(3, 8, 336);
    }

    void testWithoutDuplicates(final int count, final int numSymbols, final long expected) {
        final long value = Task01.withoutDuplicates(count, numSymbols);
        assertEquals(expected, value);
    }
}