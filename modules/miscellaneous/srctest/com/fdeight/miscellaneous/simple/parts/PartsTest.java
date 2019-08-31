package com.fdeight.miscellaneous.simple.parts;

import org.junit.Assert;
import org.junit.Test;

public class PartsTest {

    @Test
    public void testCompute01() {
        testCompute(new int[]{1, 2, 3, 4}, 2);
    }

    private void testCompute(final int[] values, final int numParts) {
        final Parts parts = new Parts(values, numParts);
        final int[] results = parts.compute(values);
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
    public void testCompute02() {
        testCompute(new int[]{1, 2, 6, 3}, 2);
    }

    @Test
    public void testCompute03() {
        testCompute(new int[]{1, 2, 6, 3, 1, 5}, 3);
    }

    @Test
    public void testCompute04() {
        testCompute(new int[]{1, 2, 6, 4, 1, 5, 3, 2}, 4);
    }

    @Test
    public void testCompute05() {
        testCompute(new int[]{-20, -20}, 2);
    }

    @Test
    public void testCompute06() {
        testCompute(new int[]{-1, 2, 3, 6}, 2);
    }

    @Test
    public void testCompute07() {
        final int[] values = new int[]{6, 20, 7, 6, 5, 6, -4, 5, 14, -1, 10, 24, -5, 7};
        final int numParts = 5;
        testCompute(values, numParts);
    }

    @Test
    public void testNotComputed01() {
        final int[] values = new int[]{1, 2, 3, 5};
        final int numParts = 2;
        testNotComputed(values, numParts);
    }

    private void testNotComputed(final int[] values, final int numParts) {
        final Parts parts = new Parts(values, numParts);
        final int[] results = parts.compute(values);
        Assert.assertNotNull("results is null", results);
        Assert.assertArrayEquals("results is not empty", new int[]{}, results);
    }


    @Test
    public void testNotComputed02() {
        final int[] values = new int[]{1, 2, 6, 8};
        final int numParts = 2;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputede03() {
        final int[] values = new int[]{2, 2, 6, 3, 1, 5};
        final int numParts = 3;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed04() {
        final int[] values = new int[]{1, 2, 6, 4, -1, 5, 3, 2};
        final int numParts = 4;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed05() {
        final int[] values = new int[]{-20, -21};
        final int numParts = 2;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed06() {
        final int[] values = new int[]{-1, -3, 3, 6};
        final int numParts = 2;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed07() {
        final int[] values = new int[]{-1, -2, 3, 6};
        final int numParts = 3;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed08() {
        final int[] values = new int[]{0, -1, -2, 3, 6, -4, 7, -9, 12, 5, -2, 1, 2, 0, 0, 3, 4, 16, 0};
        final int numParts = 3;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed09() {
        final int[] values = new int[]{10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed10() {
        final int[] values = new int[]{10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed11() {
        final int[] values = new int[]{5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        final int numParts = 4;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed12() {
        final int[] values = new int[]{5, 5, 3, 3, 3, 3, 3, 5, 5, 3, 3, 3, 3, 3};
        final int numParts = 5;
        testNotComputed(values, numParts);
    }

    @Test
    public void testNotComputed13() {
        final int[] values = new int[]{5, 5, 0, 0, 0, 0, 0, 5, 5, 3, 3, 3, 3, 3};
        final int numParts = 5;
        testNotComputed(values, numParts);
    }
}