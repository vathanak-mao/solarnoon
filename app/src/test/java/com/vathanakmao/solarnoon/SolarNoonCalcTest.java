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
    public static final double ASSERTEQUAlS_DOUBLE_DELTA = 0.00000000000000001;

    public SolarNoonCalc calc = SolarNoonCalc.getInstance();

    @Test
    public void testGetTime() {
        LocalTime solarNoonTime = calc.getTime(40L, -105L, 7, new GregorianCalendar());
        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }

    @Test
    public void testGetEquationOfTime() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(-14.23, calc.getEquationOfTime(february122024, 7), 0.01);
    }

    @Test
    public void testGetVarY() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.04, calc.getVarY(february122024, 7), 0.000001);
    }

    @Test
    public void testGetObliqCorrInDegrees() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(23.44, calc.getObliqCorrInDegrees(february122024, 7), 0.01);
    }

    @Test
    public void testGetMeanObliqEclipticInDegrees() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(23.44, calc.getMeanObliqEclipticInDegrees(february122024, 7), 0.01);
    }

    @Test
    public void testGetGeomMeanLongSun() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(321.27, calc.getGeomMeanLongSun(february122024, 7), 0.01);
    }

    @Test
    public void testGetGeomMeanAnomSun() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(9037.92, calc.getGeomMeanAnomSun(february122024, 7), 0.01);
    }

    @Test
    public void testGetEccentEarthOrbit() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.0166984903214056, calc.getEccentEarthOrbit(february122024, 7), ASSERTEQUAlS_DOUBLE_DELTA);
    }

    @Test
    public void testGetJulianCentury() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.24112834, calc.getJulianCentury(february122024, 7), ASSERTEQUAlS_DOUBLE_DELTA);
    }

    @Test
    public void testGetJulianDay() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        int timezoneOffsetFromUtc = 7;
        assertEquals(2460352.2125, calc.getJulianDay(feb122024, timezoneOffsetFromUtc), 0.00001);
    }

    @Test
    public void getNumOfDaysSince1900() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(45334L, calc.getNumOfDaysSince1900(feb122024));
    }

    @Test
    public void testGetTimePastLocalMidnight() {
        assertEquals(0.00416666666666667, calc.getTimePastLocalMidnight(), ASSERTEQUAlS_DOUBLE_DELTA);
    }

    @Test
    public void test7DividedBy24() {
        assertEquals(0, Double.valueOf(7 / 24), ASSERTEQUAlS_DOUBLE_DELTA);
        assertEquals(0.291666666666667, MathUtil.to15SignificantDigits(7D / 24), ASSERTEQUAlS_DOUBLE_DELTA);
    }
}
