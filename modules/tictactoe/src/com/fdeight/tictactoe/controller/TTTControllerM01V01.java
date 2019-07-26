package com.fdeight.tictactoe.controller;

import com.fdeight.tictactoe.model.controller.TTTController;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.TTTModel01;
import com.fdeight.tictactoe.model.controller.TTTControllerM01;
import com.fdeight.tictactoe.view.TTTView01;

public class TTTControllerM01V01 implements TTTController, TTTControllerM01, TTTControllerV01 {

    private final TTTModel01 model;

    private final TTTView01 view;

    //------------------------------------------------------------------------------------------------------------------

    public TTTControllerM01V01(final TTTModel01 model, final TTTView01 view) {
        this.model = model;
        model.setController(this);
        this.view = view;
        view.setController(this);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getVersion() {
        return String.format("Model: %s, View: %s", model.getVersion(), view.getVersion());
    }

    @Override
    public void run() {
        view.run();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public int getSize() {
        return model.getSize();
    }

    @Override
    public void startOfGame(final int input) {
        model.startOfGame(input);
    }

    //------------------------------------------------------------------------------------------------------------------


    @Override
    public void drawField(final TTTInfo state) {
        view.drawField(state);
    }

    @Override
    public TTTInfo getAction(final int playerId) {
        return view.getAction(playerId);
    }

    @Override
    public void endOfGame(final String state) {
        view.endOfGame(state);
    }
}
