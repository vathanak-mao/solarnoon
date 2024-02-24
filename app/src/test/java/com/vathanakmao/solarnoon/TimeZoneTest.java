package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class TimeZoneTest {

    @Test
    public void retrieveTimezoneOffset() {
        // https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/
        assertEquals(7, MathUtil.toHours(ZonedDateTime.now(ZoneId.of("Asia/Phnom_Penh")).getOffset().getTotalSeconds()), 0.01);
        assertEquals(-7, MathUtil.toHours(ZonedDateTime.now(ZoneId.of("US/Arizona")).getOffset().getTotalSeconds()), 0.01);
    }
}
