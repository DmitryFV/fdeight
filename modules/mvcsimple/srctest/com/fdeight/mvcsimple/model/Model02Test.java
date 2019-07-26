package com.fdeight.mvcsimple.model;

import org.junit.Assert;
import org.junit.Test;

public class Model02Test {

    @Test
    public void solving() {
        final Model02 model = new Model02();
        final String output1 = model.solving(-1);
        Assert.assertEquals("input: -1", "Input < 0", output1);
        final String output2 = model.solving(0);
        Assert.assertEquals("input: 0", "1", output2);
        final String output3 = model.solving(1);
        Assert.assertEquals("input: 1", "1", output3);
        final String output4 = model.solving(2);
        Assert.assertEquals("input: 2", "2", output4);
        final String output5 = model.solving(5);
        Assert.assertEquals("input: 3", "120", output5);
        final String output6 = model.solving(20);
        Assert.assertEquals("input: 20", "2432902008176640000", output6);
        final String output7 = model.solving(21);
        Assert.assertEquals("input: 21", "Input > 20", output7);
        System.out.println("Done");
    }
}