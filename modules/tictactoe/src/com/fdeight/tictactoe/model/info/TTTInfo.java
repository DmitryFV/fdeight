package com.fdeight.tictactoe.model.info;

public interface TTTInfo {

    String getState();

    TTTInfo getCopy();

    int[] parseField(final int size);

    int[] parseAction();
}
