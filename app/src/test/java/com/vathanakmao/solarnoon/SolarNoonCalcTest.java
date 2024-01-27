package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Date;

public class SolarNoonCalcTest {
    public SolarNoonCalc calc = new SolarNoonCalc();

    @Test
    public void testGetTime() {
        LocalTime solarNoonTime = calc.getTime(40L, -105L, 7, new Date());
        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }
}
