package com.fdeight.tictactoe;

import com.fdeight.tictactoe.model.controller.TTTController;
import com.fdeight.tictactoe.controller.TTTControllerM02V02;

public class TTT {

    public static void main(final String[] args) {
        final TTTController controller = new TTTControllerM02V02();
        controller.run();
    }
}
