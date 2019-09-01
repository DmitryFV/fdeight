package com.fdeight.miscellaneous.simple.parts;

import org.junit.Assert;
import org.junit.Test;

public class PartsTest {
    @Test
    public void testFullComputed01() {
        testComputed(PartsFull.factory, new int[]{1, 2, 3, 4}, 2);
    }

    @SuppressWarnings("SameParameterValue")
    private void testComputed(final Parts.PartsFactory partsFactory, final int[] values, final int numParts) {
        final Parts parts = partsFactory.createParts(values, numParts);
        final int[] results = parts.compute();
        Assert.assertNotNull("results is null", results);
        Assert.assertEquals("values.length != results.length",
                values.length, results.length);
        final int[] sum = new int[numParts];
        for (int i = 0; i < values.length; i++) {
            sum[results[i]] += values[i];
        }
        for (int part = 1; part < numParts; part++) {
            Assert.assertEquals(String.format("part: %d", part), sum[0], sum[part]);
        }
    }

    @Test
    public void testFullComputed02() {
        testComputed(PartsFull.factory, new int[]{1, 2, 6, 3}, 2);
    }

    @Test
    public void testFullComputed03() {
        testComputed(PartsFull.factory, new int[]{1, 2, 6, 3, 1, 5}, 3);
    }

    @Test
    public void testFullComputed04() {
        testComputed(PartsFull.factory, new int[]{1, 2, 6, 4, 1, 5, 3, 2}, 4);
    }

    @Test
    public void testFullComputed05() {
        testComputed(PartsFull.factory, new int[]{-20, -20}, 2);
    }

    @Test
    public void testFullComputed06() {
        testComputed(PartsFull.factory, new int[]{-1, 2, 3, 6}, 2);
    }

    @Test
    public void testFullComputed07() {
        final int[] values = new int[]{6, 20, 7, 6, 5, 6, -4, 5, 14, -1, 10, 24, -5, 7};
        final int numParts = 5;
        testComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullComputed08() {
        final int[] values = new int[]{-2, -1, -3, -8, -5, -1, -4, -6, -3, -6, -10, -2, -2, -2};
        final int numParts = 5;
        testComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed01() {
        final int[] values = new int[]{1, 2, 3, 5};
        final int numParts = 2;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @SuppressWarnings("SameParameterValue")
    private void testNotComputed(final Parts.PartsFactory partsFactory, final int[] values, final int numParts) {
        final Parts parts = partsFactory.createParts(values, numParts);
        final int[] results = parts.compute();
        Assert.assertNotNull("results is null", results);
        Assert.assertArrayEquals("results is not empty", new int[]{}, results);
    }

    @Test
    public void testFullNotComputed02() {
        final int[] values = new int[]{1, 2, 6, 8};
        final int numParts = 2;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed03() {
        final int[] values = new int[]{2, 2, 6, 3, 1, 5};
        final int numParts = 3;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed04() {
        final int[] values = new int[]{1, 2, 6, 4, -1, 5, 3, 2};
        final int numParts = 4;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed05() {
        final int[] values = new int[]{-20, -21};
        final int numParts = 2;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed06() {
        final int[] values = new int[]{-1, -3, 3, 6};
        final int numParts = 2;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed07() {
        final int[] values = new int[]{-1, -2, 3, 6};
        final int numParts = 3;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed08() {
        final int[] values = new int[]{0, -1, -2, 3, 6, -4, 7, -9, 12, 5, -2, 1, 2, 0, 0, 3, 4, 16, 0};
        final int numParts = 3;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed09() {
        final int[] values = new int[]{10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed10() {
        final int[] values = new int[]{10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed11() {
        final int[] values = new int[]{5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed12() {
        final int[] values = new int[]{5, 5, 3, 3, 3, 3, 3, 5, 5, 3, 3, 3, 3, 3};
        final int numParts = 5;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testFullNotComputed13() {
        final int[] values = new int[]{5, 5, 0, 0, 0, 0, 0, 5, 5, 3, 3, 3, 3, 3};
        final int numParts = 5;
        testNotComputed(PartsFull.factory, values, numParts);
    }

    @Test
    public void testDynamicNotComputed01() {
        final int[] values = new int[]{1, 2, 3, 5};
        final int numParts = 2;
        testNotComputed(PartsDynamic.factory, values, numParts);
    }
}