package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * All the test data is in the excel file named NOAA_Solar_Calculations_day.ods,
 * downloaded from https://gml.noaa.gov/grad/solcalc/.
 */
public class SolarNoonCalcTest {
    public SolarNoonCalc calc = SolarNoonCalc.getInstance();

    @Test
    public void testGetTime() {
        LocalTime solarNoonTime = calc.getTime(40L, -105L, 7, new GregorianCalendar());
        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }

    @Test
    public void testGetGeomMeanLongSun() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(321.27, calc.getGeomMeanLongSun(february122024, 7), 0.01);
    }

    @Test
    public void testGetEccentEarthOrbit() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.02, calc.getEccentEarthOrbit(february122024, 7), 0.01);
    }

    @Test
    public void testGetJulianCentury() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.24112834, calc.getJulianCentury(february122024, 7), 0.00000001);
    }

    @Test
    public void testGetJulianDay() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        int timezoneOffsetFromUtc = 7;
        assertEquals(2460352.21, calc.getJulianDay(feb122024, timezoneOffsetFromUtc), 0.01);
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

    @Test
    public void test7DividedBy24() {
        assertEquals(0, Double.valueOf(7 / 24), 0.00000000001);
        assertNotEquals(0.291666666666667, 7F / 24, 0.000000000001);
        assertEquals(0.291666666666667, 7D / 24, 0.000000000001);

    }
}
