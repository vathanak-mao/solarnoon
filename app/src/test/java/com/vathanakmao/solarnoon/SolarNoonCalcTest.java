package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.vathanakmao.solarnoon.model.LocalTime;
import com.vathanakmao.solarnoon.service.SolarNoonCalc;
import com.vathanakmao.solarnoon.util.MathUtil;

import org.junit.Test;

import java.util.GregorianCalendar;

/**
 * All the test data is in the excel file named NOAA_Solar_Calculations_day.ods,
 * downloaded from https://gml.noaa.gov/grad/solcalc/.
 */
public class SolarNoonCalcTest {
    public static final double ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17 = 0.00000000000000001;
    public static final double ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10 = 0.0000000001;

    public SolarNoonCalc calc = new SolarNoonCalc();

    @Test
    public void testGetTime() {
        // Latitude and longitude belongs to Phnom Penh
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        LocalTime solarNoonTime = calc.getTime(11.57, 104.935913, 7, february122024, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00);
        assertEquals(12, solarNoonTime.getHour());
        assertEquals(14, solarNoonTime.getMinute());
        assertEquals(29, solarNoonTime.getSecond());

        final GregorianCalendar february292024 = new GregorianCalendar(2024, 1, 29);
        LocalTime solarNoonTime2 = calc.getTime(37.4234234234234, -122.083952878678, -8, february292024, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00);
        assertEquals(12, solarNoonTime2.getHour());
        assertEquals(20, solarNoonTime2.getMinute());
        assertEquals(46, solarNoonTime2.getSecond());
    }

    @Test
    public void testGetEquationOfTime() {
        // As long as the first 10 digits to the right of the decimal point
        // are correct, the return value from the getTime() method is correct.
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(-14.2301766538485, MathUtil.to15SignificantDigits(calc.getEquationOfTime(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(-14.2301852029459, MathUtil.to15SignificantDigits(calc.getEquationOfTime(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(-14.2301935276888, MathUtil.to15SignificantDigits(calc.getEquationOfTime(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
    }

    @Test
    public void testGetVarY() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.0430318468389719, MathUtil.to15SignificantDigits(calc.getVarY(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.0430318468452841, MathUtil.to15SignificantDigits(calc.getVarY(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.0430318468515961, MathUtil.to15SignificantDigits(calc.getVarY(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

    @Test
    public void testGetObliqCorrInDegrees() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(23.4385807947548, MathUtil.to15SignificantDigits(calc.getObliqCorrInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(23.4385807964263, MathUtil.to15SignificantDigits(calc.getObliqCorrInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(23.4385807980978, MathUtil.to15SignificantDigits(calc.getObliqCorrInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

    @Test
    public void testGetMeanObliqEclipticInDegrees() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(23.4361554355635, MathUtil.to15SignificantDigits(calc.getMeanObliqEclipticInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(23.43615543408, MathUtil.to15SignificantDigits(calc.getMeanObliqEclipticInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(23.4361554325965, MathUtil.to15SignificantDigits(calc.getMeanObliqEclipticInDegrees(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

    @Test
    public void testGetGeomMeanLongSun() {
        // As long as the first 10 digits to the right of the decimal point
        // are correct, the return value from the getTime() method is correct.
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(321.272228660055, calc.getGeomMeanLongSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(321.276335523815, calc.getGeomMeanLongSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(321.280442388044, calc.getGeomMeanLongSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
    }

    @Test
    public void testGetGeomMeanAnomSun() {
        // As long as the first 10 digits to the right of the decimal point
        // are correct, the return value from the getTime() method is correct.
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(9037.92022227439, MathUtil.to15SignificantDigits(calc.getGeomMeanAnomSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(9037.92432894197, MathUtil.to15SignificantDigits(calc.getGeomMeanAnomSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
        assertEquals(9037.92843561001, MathUtil.to15SignificantDigits(calc.getGeomMeanAnomSun(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF10);
    }

    @Test
    public void testGetEccentEarthOrbit() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(0.0166984903214056, MathUtil.to15SignificantDigits(calc.getEccentEarthOrbit(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.0166984903166031, MathUtil.to15SignificantDigits(calc.getEccentEarthOrbit(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.0166984903118007, MathUtil.to15SignificantDigits(calc.getEccentEarthOrbit(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_18_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

    @Test
    public void testGetJulianCentury() {
        final GregorianCalendar february122024 = new GregorianCalendar(2024, 1, 12);

        // In the Excel file, each cell is formatted to display only 8 decimal places.
        // But, its value with all available decimal places (or precisions)
        // is used instead in the calculation of anther cell referencing it.
        // For example, Geom Mean Long Sun (deg) column uses below value instead of the formatted one above.
        assertEquals(0.24112834, MathUtil.roundTo8DecimalPlaces(calc.getJulianCentury(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.241128336755657, MathUtil.to15SignificantDigits(calc.getJulianCentury(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);

        assertEquals(0.24112845, MathUtil.roundTo8DecimalPlaces(calc.getJulianCentury(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.241128450832766, MathUtil.to15SignificantDigits(calc.getJulianCentury(february122024, 7, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

    @Test
    public void testGetJulianDay() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        int timezoneOffsetFromUtc = 7;

        // Time Past Local Midnight: 00:06:00
        assertEquals(2460352.2125000004, calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(2460352.2125, MathUtil.to15SignificantDigits(calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(2460352.21, MathUtil.round(calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00), 2), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);

        // Time Past Local Midnight: 00:12:00
        assertEquals(2460352.216666667, calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17); // the scale of 2
        assertEquals(2460352.21666667, MathUtil.to15SignificantDigits(calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00)), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(2460352.22, MathUtil.round(calc.getJulianDay(feb122024, timezoneOffsetFromUtc, SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00), 2), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17); // the scale of 2
    }

    @Test
    public void getNumOfDaysSince1900() {
        GregorianCalendar feb122024 = new GregorianCalendar(2024, 1, 12);
        assertEquals(45334L, calc.getNumOfDaysSince1900(feb122024));
    }

    @Test
    public void testGetTimePastLocalMidnight() {
        assertEquals(0.00416666666666667, MathUtil.to15SignificantDigits(SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_06_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
        assertEquals(0.00833333333333333, MathUtil.to15SignificantDigits(SolarNoonCalc.TIMEPASTLOCALMIDNIGHT_00_12_00), ASSERTEQUAlS_DOUBLE_DELTA_SCALEOF17);
    }

}
