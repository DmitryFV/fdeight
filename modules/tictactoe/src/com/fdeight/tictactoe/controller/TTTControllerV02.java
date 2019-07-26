package com.fdeight.tictactoe.controller;

import com.fdeight.tictactoe.model.controller.TTTController;

public interface TTTControllerV02 extends TTTController {

    int getSize();

    void startOfGame(final int size);
}
