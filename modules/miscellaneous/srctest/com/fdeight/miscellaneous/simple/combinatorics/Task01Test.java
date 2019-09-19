package com.fdeight.miscellaneous.simple.combinatorics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Task01Test {

    @Test
    void testWithDublicates() {
        final int count = 6;
        final int numSymbols = 10;
        final long value1 = Task01.withDublicates(count, numSymbols);
        final long value2 = Task01.withDublicatesAll(count, numSymbols);
        assertEquals(value1, value2);
    }

    @Test
    void testWithDublicates01() {
        testWithDublicates(4, 7, 2401);
    }

    @Test
    void testWithDublicates02() {
        testWithDublicates(3, 8, 512);
    }

    void testWithDublicates(final int count, final int numSymbols, final long expected) {
        final long value = Task01.withDublicates(count, numSymbols);
        assertEquals(expected, value);
    }

    @Test
    void testWithoutDublicatess() {
        final int count = 6;
        final int numSymbols = 10;
        final long value1 = Task01.withoutDublicates(count, numSymbols);
        final long value2 = Task01.withoutDublicatesAll(count, numSymbols);
        assertEquals(value1, value2);
    }

    @Test
    void testWithoutDublicates01() {
        testWithoutDublicates(4, 7, 840);
    }

    @Test
    void testWithoutDublicates02() {
        testWithoutDublicates(3, 8, 336);
    }

    void testWithoutDublicates(final int count, final int numSymbols, final long expected) {
        final long value = Task01.withoutDublicates(count, numSymbols);
        assertEquals(expected, value);
    }
}