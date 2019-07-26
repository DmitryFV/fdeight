package com.fdeight.tictactoe.controller;

import com.fdeight.tictactoe.model.controller.TTTController;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.model.TTTModel02;
import com.fdeight.tictactoe.model.controller.TTTControllerM02;
import com.fdeight.tictactoe.view.TTTView01;

public class TTTControllerM02V01 implements TTTController, TTTControllerM02, TTTControllerV01 {

    private final TTTModel02 model;

    private final TTTView01 view;

    //------------------------------------------------------------------------------------------------------------------

    public TTTControllerM02V01() {
        this.model = new TTTModel02();
        model.setController(this);
        this.view = new TTTView01();
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
    public void setAction(final int playerId, final TTTInfo action) {
        view.setAction(playerId, action);
    }

    @Override
    public void endOfGame(final String state) {
        view.endOfGame(state);
    }
}
