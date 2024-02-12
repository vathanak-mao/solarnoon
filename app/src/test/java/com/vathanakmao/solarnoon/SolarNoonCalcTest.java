package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Date;
import java.util.GregorianCalendar;

public class SolarNoonCalcTest {
    public SolarNoonCalc calc = SolarNoonCalc.getInstance();

    @Test
    public void testGetTime() {
        LocalTime solarNoonTime = calc.getTime(40L, -105L, 7, new GregorianCalendar());
        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }

    @Test
    public void getNumOfDaysSince1900() {
        GregorianCalendar feb122024 = new GregorianCalendar();
        feb122024.set(2024, 1, 12);

        // In Excel, 2 more days are added when using functions such as DAY() and WEEKDAY().
        // REFERENCES:
        //      - https://calculat.io/en/date/how-many-until/1-january-1900
        //      - https://www.epochconverter.com/seconds-days-since-y0#:~:text=There%20were%2045332%20days%20since%20January%201%2C%201900.
        assertEquals(45332L, calc.getNumOfDaysSince1900(feb122024));
    }
}
