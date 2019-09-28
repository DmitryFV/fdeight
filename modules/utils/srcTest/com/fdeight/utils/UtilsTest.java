package com.fdeight.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void testCheckArgument01() {
        Utils.checkArgument(2, 2, 2, "01");
    }

    @Test
    void testCheckArgument02() {
        Utils.checkArgument(-2, -2, -5, "02");
    }

    @Test
    void testCheckArgument03() {
        Utils.checkArgument(2, 2, 5, "03");
    }

    @Test
    void testCheckArgument04() {
        Utils.checkArgument(4, 10, 2, "04");
    }

    @Test
    void testCheckArgument05() {
        Utils.checkArgument(0, Integer.MIN_VALUE, Integer.MAX_VALUE, "05");
    }

    @Test
    void testCheckArgumentThrows01() {
        assertThrows(IllegalArgumentException.class, () ->
                Utils.checkArgument(-1, -2, -3, "01"));
    }

    @Test
    void testCheckArgumentThrows02() {
        assertThrows(IllegalArgumentException.class, () ->
                Utils.checkArgument(0, -1, -1, "02"));
    }

    @Test
    void testCheckArgumentThrows03() {
        assertThrows(IllegalArgumentException.class, () ->
                Utils.checkArgument(11, 3, 10, "03"));
    }

    @Test
    void testCheckArgumentThrows04() {
        assertThrows(IllegalArgumentException.class, () ->
                Utils.checkArgument(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE + 1, "04"));
    }

    @Test
    void testCheckArgumentThrows05() {
        assertThrows(IllegalArgumentException.class, () ->
                Utils.checkArgument(Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE,
                        "05"));
    }
}