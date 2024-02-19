package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

public class LocalTimeTest {

    @Test
    public void testLocalTime() {
        final LocalTime time = new LocalTime(0.510060092502);
        assertEquals(12, time.getHour());
        assertEquals(14, time.getMinute());
        assertEquals(29, time.getSecond());
    }
}
