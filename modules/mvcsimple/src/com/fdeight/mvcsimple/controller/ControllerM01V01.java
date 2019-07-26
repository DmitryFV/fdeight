package com.fdeight.mvcsimple.controller;

import com.fdeight.mvcsimple.model.Model01;
import com.fdeight.mvcsimple.model.controller.Controller;
import com.fdeight.mvcsimple.view.View01;

/**
 * Контроллер.
 * Создание ControllerM01 не требуется, поскольку модель не использует контроллер.
 */
public class ControllerM01V01 implements Controller, ControllerV01 {

    private final Model01 model;

    private final View01 view;

    //------------------------------------------------------------------------------------------------------------------

    public ControllerM01V01(final Model01 model, final View01 view) {
        this.model = model;
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
        throw new UnsupportedOperationException("Unsupported startOfSolving()");
    }
}
