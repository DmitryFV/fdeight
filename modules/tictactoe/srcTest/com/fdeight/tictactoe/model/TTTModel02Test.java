package com.fdeight.tictactoe.model;

import com.fdeight.tictactoe.model.controller.TTTControllerM02;
import com.fdeight.tictactoe.model.info.TTTInfo;
import org.junit.Assert;
import org.junit.Test;

public class TTTModel02Test {

    @Test
    public void getSize() {
        final TTTModel02 tttModel02 = new TTTModel02();
        Assert.assertEquals(0, tttModel02.getSize());
        tttModel02.setController(new TTTControllerM02() {
            @Override
            public void drawField(final TTTInfo state) {
            }

            @Override
            public TTTInfo getAction(final int playerId) {
                return null;
            }

            @Override
            public void setAction (final int playerId, final TTTInfo action) {
            }

            @Override
            public void endOfGame(final String state) {
            }

            @Override
            public String getVersion() {
                return null;
            }

            @Override
            public void run() {
            }
        });
        tttModel02.initGame(3);
        Assert.assertEquals(3, tttModel02.getSize());
    }

    @Test
    public void getVersion() {
        Assert.assertEquals("TTT 2.0", TTTModel02.getVersion());
    }
}