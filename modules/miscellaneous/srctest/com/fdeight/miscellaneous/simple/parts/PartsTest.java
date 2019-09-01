package com.fdeight.miscellaneous.simple.parts;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PartsTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFullComputed01() {
        testComputed(PartsFull.factory, new int[]{1, 2, 3, 4}, 2);
    }

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
        testComputed(PartsFull.factory, new int[]{1, 2, 6, 0, 0, 0, 3, 1, 5}, 3);
    }

    @Test
    public void testFullComputed04() {
        testComputed(PartsFull.factory, new int[]{1, 2, 6, 4, 1, 5, 3, 2, 0, 0}, 4);
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
        final int[] values = new int[]{6, 20, 7, 6, 5, 0, 0, 0, 6, -4, 5, 14, -1, 10, 24, -5, 7};
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
    public void testFullComputed09() {
        testComputed(PartsFull.factory,
                new int[]{4, 4, 1, 2, 3, 4, 10, 99, 98, 1,
                        25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
                        25, 1, 24, 0, 0, 1, 2, 3, 4, 10, 5, -25, 50, -1, 2, -2, 3, 23,
                },
                2);
    }

    @Test
    public void testFullComputed10() {
        testComputed(PartsFull.factory,
                new int[]{10750, 10750, 4, 4, 1, 2, 3, 4, 10, 0, 0, 0, 99, 98, 1},
                2);
    }

    @Test
    public void testFullNotComputed01() {
        final int[] values = new int[]{1, 2, 3, 5};
        final int numParts = 2;
        testNotComputed(PartsFull.factory, values, numParts);
    }

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
        final int[] values = new int[]{4, 4, 3, 3, 3, 3, 4, 4, 3, 3, 3, 3, 5};
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
    public void testDynamicExceptionNumParts() {
        thrown.expect(IllegalArgumentException.class);
        PartsDynamic.factory.createParts(new int[]{1, 1}, 3);
    }

    @Test
    public void testDynamicExceptionNegativeValue() {
        thrown.expect(IllegalArgumentException.class);
        PartsDynamic.factory.createParts(new int[]{1, -1}, 2);
    }

    @Test
    public void testDynamicComputed01() {
        testComputed(PartsDynamic.factory, new int[]{1, 2, 3, 4}, 2);
    }

    @Test
    public void testDynamicComputed02() {
        testComputed(PartsDynamic.factory, new int[]{1, 2, 6, 0, 0, 0, 0, 3}, 2);
    }

    @Test
    public void testDynamicComputed03() {
        testComputed(PartsDynamic.factory, new int[]{2, 6, 3, 2, 0, 6, 3, 2, 3, 0, 0, 0, 4, 2, 3, 4}, 2);
    }

    @Test
    public void testDynamicComputed04() {
        testComputed(PartsDynamic.factory, new int[]{30, 40, 50, 10, 12, 12, 8, 8, 5, 5}, 2);
    }

    @Test
    public void testDynamicComputed05() {
        testComputed(PartsDynamic.factory,
                new int[]{10750, 10750, 4, 4, 1, 2, 3, 4, 10, 99, 98, 1,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 12, 3, 23,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 12, 3, 23,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 32, 3, 24,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 32, 3, 24,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 52, 3, 24,
                        25, 1, 24, 10, 10, 1, 2, 3, 4, 0, 0, 0, 10, 5, 25, 50, 1, 2, 52, 3, 24,
                        55, 1, 24, 12, 10, 1, 2, 3, 4, 0, 0, 0, 16, 5, 25, 50, 1, 2, 62, 3, 23,
                        55, 1, 24, 12, 10, 1, 2, 3, 4, 0, 0, 0, 16, 5, 25, 50, 1, 2, 62, 3, 23,
                        55, 1, 24, 10, 11, 1, 2, 3, 4, 0, 0, 0, 16, 5, 75, 50, 1, 2, 72, 3, 23,
                        55, 1, 24, 10, 11, 1, 2, 3, 4, 0, 0, 0, 16, 5, 75, 50, 1, 2, 72, 3, 23,
                },
                2);
    }

    @Test
    public void testDynamicComputed06() {
        testComputed(PartsDynamic.factory,
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                2);
    }

    @Test
    public void testDynamicComputed07() {
        testComputed(PartsDynamic.factory,
                new int[]{10750, 99, 10750, 4, 4, 1, 2, 3, 4, 10, 98, 1},
                2);
    }

    @Test
    public void testDynamicNotComputed01() {
        final int[] values = new int[]{1, 2, 3, 5};
        final int numParts = 2;
        testNotComputed(PartsDynamic.factory, values, numParts);
    }

    @Test
    public void testDynamicNotComputed02() {
        testNotComputed(PartsDynamic.factory,
                new int[]{10, 10, 20, 20, 30, 30, 1, 49},
                2
        );
    }

    @Test
    public void testDynamicNotComputed03() {
        testNotComputed(PartsDynamic.factory,
                new int[]{30, 40, 50, 0, 10, 12, 12, 0, 0, 8, 8},
                2
        );
    }
}