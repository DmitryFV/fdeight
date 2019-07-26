package com.fdeight.tictactoe.view;

import com.fdeight.tictactoe.controller.TTTControllerV02;
import com.fdeight.tictactoe.model.info.TTTInfo;
import com.fdeight.tictactoe.view.graphic.MainWindow;

public class TTTView02 {

    /**
     * Контроллер.
     */
    private TTTControllerV02 controller;

    private MainWindow mainWindow;

    public TTTView02() {
        mainWindow = null;
    }

    public void setController(final TTTControllerV02 controller) {
        this.controller = controller;
    }

    public String getVersion() {
        return "TTT 2.0";
    }

    public void run() {
        mainWindow();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void mainWindow() {
        mainWindow = new MainWindow(controller.getVersion(), this);
    }

    public void drawField(final TTTInfo info) {
        mainWindow.setInfo(info);
    }

    public TTTInfo getAction(final int playerId) {
        return mainWindow.getAction(playerId);
    }

    public void setAction(final int playerId, final TTTInfo action) {
        mainWindow.setAction(playerId, action);
    }

    public int getSize() {
        return controller.getSize();
    }

    public void startOfGame(final int size) {
        controller.startOfGame(size);
    }

    public void endOfGame(final String state) {
        mainWindow.endOfGame(state);
    }
}
