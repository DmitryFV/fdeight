package com.fdeight.mvcsimple.controller;

import com.fdeight.mvcsimple.model.Model02;
import com.fdeight.mvcsimple.view.View01;

public class ControllerM02V01 implements Controller, ControllerM02, ControllerV01 {

    private final Model02 model;

    private final View01 view;

    //------------------------------------------------------------------------------------------------------------------

    public ControllerM02V01(final Model02 model, final View01 view) {
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
    public String getData() {
        return model.getData();
    }

    @Override
    public void startOfSolving(final long input) {
        model.startOfSolving(input);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void endOfSolving(final String output) {
        view.endOfSolving(output);
    }
}
