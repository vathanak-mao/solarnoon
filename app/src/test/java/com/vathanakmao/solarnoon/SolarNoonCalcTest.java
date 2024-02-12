package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * All the test data should be from the excel file downloaded from https://gml.noaa.gov/grad/solcalc/.
 */
public class SolarNoonCalcTest {
    public SolarNoonCalc calc = SolarNoonCalc.getInstance();

    @Test
    public void testGetTime() {
        LocalTime solarNoonTime = calc.getTime(40L, -105L, 7, new GregorianCalendar());
        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }

    @Test
    public void testGetJulianDay() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        int timezoneOffsetFromUtc = 7;
        assertEquals(2460352.21, calc.getJulianDay(feb122024, timezoneOffsetFromUtc), 1.0);
    }

    @Test
    public void getNumOfDaysSince1900() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(45334L, calc.getNumOfDaysSince1900(feb122024));
    }

    @Test
    public void testGetTimePastLocalMidnight() {
        assertEquals(0.00416666666666667, calc.getTimePastLocalMidnight(),0.000000001);
    }
}
