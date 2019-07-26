package com.fdeight.mvcsimple.model;

import com.fdeight.mvcsimple.model.controller.ControllerM02;

public class Model02 {

    private static final String DATA = "02";

    private ControllerM02 controller;

    //------------------------------------------------------------------------------------------------------------------

    public void setController(final ControllerM02 controller) {
        this.controller = controller;
    }

    public String getVersion() {
        return "2.0";
    }

    //------------------------------------------------------------------------------------------------------------------

    public String getData() {
        return DATA;
    }

    public void startOfSolving(final long input) {
        final String output = solving(input);
        controller.endOfSolving(output);
    }

    //------------------------------------------------------------------------------------------------------------------

   String solving(final long input) {
        if (input < 0) {
            return "Input < 0";
        } else if (input > 20) {
            return "Input > 20";
        } else {
            final long result;
            if (input < 2) {
                result = 1;
            } else {
                long localResult = 1;
                for (int i = 2; i <= input; i++) {
                    localResult *= i;
                }
                result = localResult;
            }
            return Long.toString(result);
        }
    }
}
