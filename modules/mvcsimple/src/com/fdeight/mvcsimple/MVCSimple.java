package com.fdeight.mvcsimple;

import com.fdeight.mvcsimple.controller.Controller;
import com.fdeight.mvcsimple.controller.ControllerM01V01;
import com.fdeight.mvcsimple.controller.ControllerM02V01;
import com.fdeight.mvcsimple.model.Model01;
import com.fdeight.mvcsimple.model.Model02;
import com.fdeight.mvcsimple.view.View01;

public class MVCSimple {

    public static void main(final String[] args) {
        final Controller controller = new ControllerM02V01(new Model02(), new View01());
        controller.run();
    }
}
