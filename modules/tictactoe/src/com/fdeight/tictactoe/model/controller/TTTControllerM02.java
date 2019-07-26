package com.fdeight.tictactoe.model.controller;

import com.fdeight.tictactoe.model.info.TTTInfo;

public interface TTTControllerM02 extends TTTController {

    void drawField(final TTTInfo state);

    TTTInfo getAction(final int playerId);

    void setAction(final int playerId, final TTTInfo action);

    void endOfGame(final String state);
}
