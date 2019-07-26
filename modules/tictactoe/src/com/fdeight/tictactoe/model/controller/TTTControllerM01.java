package com.fdeight.tictactoe.model.controller;

import com.fdeight.tictactoe.model.info.TTTInfo;

public interface TTTControllerM01 extends TTTController {

    void drawField(final TTTInfo state);

    TTTInfo getAction(final int playerId);

    void endOfGame(final String state);
}
