package com.fdeight.sport.khl.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KHLMatchInfoTest {
    @Test
    public void testPeriods() {
        assertEquals(KHLMatchInfo.Periods.PERIOD1.ordinal(), 0);
        assertEquals(KHLMatchInfo.Periods.PERIOD2.ordinal(), 1);
        assertEquals(KHLMatchInfo.Periods.PERIOD3.ordinal(), 2);
        assertEquals(KHLMatchInfo.Periods.OVERTIME.ordinal(), 3);
        assertEquals(KHLMatchInfo.Periods.SHOOTOUTS.ordinal(), 4);
    }
}