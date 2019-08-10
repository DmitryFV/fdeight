package com.fdeight.tictactoe.model.info;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TTTInfo02Test {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseField0Exception() {
        final String state = "";
        final int size = 0;
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        thrown.expect(IllegalStateException.class);
        tttInfo.parseField(size);
    }

    @Test
    public void parseField1Exception() {
        final String state = "a";
        final int size = 1;
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        thrown.expect(NumberFormatException.class);
        tttInfo.parseField(size);
    }

    @Test
    public void parseField2Exception() {
        final String state = "0 1 2";
        final int size = 2;
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        thrown.expect(IllegalArgumentException.class);
        tttInfo.parseField(size);
    }

    @Test
    public void parseField2() {
        final String state = "0 0 0 0";
        final int size = 2;
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        final int[] field = tttInfo.parseField(size);
        for (final int value : field) {
            Assert.assertEquals(0, value);
        }
    }

    @Test
    public void parseField3() {
        final String state = "0 1 2 2 1 0 0 0 0";
        final int size = 3;
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        final int[] field = tttInfo.parseField(size);
        Assert.assertArrayEquals(field, new int[]{0, 1, 2, 2, 1, 0, 0, 0, 0});
    }

    @Test
    public void parseAction0() {
        final String state = "";
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        thrown.expect(IllegalStateException.class);
        tttInfo.parseAction();
    }

    @Test
    public void parseAction1() {
        final String state = "1 1";
        final TTTInfo02 tttInfo = new TTTInfo02(state);
        final int[] action = tttInfo.parseAction();
        Assert.assertArrayEquals(action, new int[]{0, 0});
    }
}