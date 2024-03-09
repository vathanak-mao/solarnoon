package com.vathanakmao.solarnoon.service;

import com.vathanakmao.solarnoon.model.LocalTime;
import com.vathanakmao.solarnoon.util.MathUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <b>Solar noon calculator.</b><br/>
 * All the calculations here are based on the Excel file NOAA_Solar_Calculations_day.ods,
 * which can be downloaded from <a href>https://gml.noaa.gov/grad/solcalc/calcdetails.html</a>.
 *
 * <lu>
 *     <li>
 *          <b>Beginning of Julian period</b> is January 1, 4713 BC (Before Christ).<br/>
 *     </li>
 *     <li>
 *          <b>Epoch</b> means a specific period of time, an era, or a stage.
 *     </li>
 *     <li>
 *          <b>Epoch J2000</b> is a standard point in time used as a reference in astronomy,
 *          which is expressed as 2000 January 1, 11:58:55.816 UTC.
 *      </li>
 * </lu>
 */
public class SolarNoonCalc {
    /**
     * The number of days from the beginning of Julian period to epoch J2000.
     * Check this class's definition for what epoch j2000 is.
     */
    public static final double JULIANDATE_FOR_EPOCHJ2000 = 2451545.0;

    /**
     * The number of days from the beginning of Julian period to December 30, 1900.
     */
    public static final double JULIANDATE_FOR_1900DEC30 = 2415018.5;

    /**
     * The number of Julian days per century (100 years).
     */
    public static final double JULIAN_DAYS_PER_CENTURY = 36525;

    public static final double TIMEPASTLOCALMIDNIGHT_00_06_00 = 0.1D/24;
    public static final double TIMEPASTLOCALMIDNIGHT_00_12_00 = TIMEPASTLOCALMIDNIGHT_00_06_00 + 0.1D/24;
    public static final double TIMEPASTLOCALMIDNIGHT_00_18_00 = TIMEPASTLOCALMIDNIGHT_00_12_00 + 0.1D/24;

//    private static SolarNoonCalc instance;

    public SolarNoonCalc() {}

//    public static SolarNoonCalc getInstance() {
//        if (instance == null) {
//            instance = new SolarNoonCalc();
//        }
//        return instance;
//    }

    /**
     * Get the local time of the specified date and location
     * that the sun is at its highest in the sky.
     *
     * @param lat - latitude of the location, for example, 11.566143 for Phnom Penh city, Cambodia.
     * @param lon - longitude of the location, for example, 104.935913 for Phnom Penh city, Cambodia.
     * @param timezoneOffsetFromUtc - timezone offset from UTC. For example, if timezone is UTC+7, then it's 7.
     * @param date - the date for the solar noon
     * @param timePastLocalMidnight
     * @return the time of the solar noon
     */
    public LocalTime getTime(double lat, double lon, double timezoneOffsetFromUtc, GregorianCalendar date, double timePastLocalMidnight) {
        final double solarTime = (720 - 4 * lon - getEquationOfTime(date, timezoneOffsetFromUtc, timePastLocalMidnight) + timezoneOffsetFromUtc * 60) / 1440;
        return new LocalTime(solarTime);
    }

    public double getEquationOfTime(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // V column
        final double geomMeanLongSun = getGeomMeanLongSun(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double geomMeanLongSun = MathUtil.to15SignificantDigits(getGeomMeanLongSun(date, timezoneOffsetFromUtc, timePastLocalMidnight));

        final double geomMeanAnomSun = getGeomMeanAnomSun(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double geomMeanAnomSun = MathUtil.to15SignificantDigits(getGeomMeanAnomSun(date, timezoneOffsetFromUtc, timePastLocalMidnight));

        final double eccentEarthOrbit = getEccentEarthOrbit(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double eccentEarthOrbit = MathUtil.to15SignificantDigits(getEccentEarthOrbit(date, timezoneOffsetFromUtc, timePastLocalMidnight));

        final double radiansOfGeomMeanLongSun = Math.toRadians(geomMeanLongSun);
//        final double radiansOfGeomMeanLongSun = Math.toRadians(geomMeanLongSun);

        final double radiansOfGeomMeanAnomSun= Math.toRadians(geomMeanAnomSun);
//        final double radiansOfGeomMeanAnomSun = Math.toRadians(geomMeanAnomSun);

//        final double varY = MathUtil.to15SignificantDigits(getVarY(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        final double varY = getVarY(date, timezoneOffsetFromUtc, timePastLocalMidnight);

        return 4 * Math.toDegrees(
                varY * Math.sin(2 * radiansOfGeomMeanLongSun)
                - 2 * eccentEarthOrbit * Math.sin(radiansOfGeomMeanAnomSun)
                + 4 * eccentEarthOrbit * varY * Math.sin(radiansOfGeomMeanAnomSun) * Math.cos(2 * radiansOfGeomMeanLongSun)
                - 0.5 * varY * varY * Math.sin(4 * radiansOfGeomMeanLongSun)
                - 1.25 * eccentEarthOrbit * eccentEarthOrbit * Math.sin(2 * radiansOfGeomMeanAnomSun)
                );
    }

    public double getVarY(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // U column
        final double obliqCorrInDegrees = getObliqCorrInDegrees(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double obliqCorrInDegrees = MathUtil.to15SignificantDigits(getObliqCorrInDegrees(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return Math.tan( Math.toRadians(obliqCorrInDegrees/2) ) * Math.tan( Math.toRadians(obliqCorrInDegrees/2) );
    }

    public double getObliqCorrInDegrees(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // R column
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double julianCentury = MathUtil.to15SignificantDigits(getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return getMeanObliqEclipticInDegrees(date, timezoneOffsetFromUtc, timePastLocalMidnight) + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * julianCentury));
    }

    public double getMeanObliqEclipticInDegrees(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // Q column
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double julianCentury = MathUtil.to15SignificantDigits(getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return 23 + (26 + ((21.448 - julianCentury * (46.815 + julianCentury * (0.00059 - julianCentury * 0.001813)))) / 60) / 60;
    }

    public double getGeomMeanLongSun(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // column I
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double julianCentury = MathUtil.to15SignificantDigits(getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return (280.46646 + julianCentury * (36000.76983 + julianCentury * 0.0003032)) % 360;
    }

    public double getGeomMeanAnomSun(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) {
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double julianCentury = MathUtil.to15SignificantDigits(getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return 357.52911 + julianCentury * (35999.05029 - 0.0001537 * julianCentury);
    }

    public double getEccentEarthOrbit(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) {
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight);
//        final double julianCentury = MathUtil.to15SignificantDigits(getJulianCentury(date, timezoneOffsetFromUtc, timePastLocalMidnight));
        return 0.016708634 - julianCentury * (0.000042037 + 0.0000001267 * julianCentury);
    }

    /**
     * Get the number of Julian centuries since the epoch J2000.
     * Check the Java doc on this class for what epoch J2000 is.
     *
     * In the Excel file, each cell in the Julian Century column was formatted to display
     * its value only with 8 decimal places (8 digits to the right of the decimal point).
     * But, when the other columns (e.g. Geom Mean Long Sun (deg)) referenced it,
     * its value with all available decimal places, before formatting,
     * (e.g. 0.241128336755644) was used in the calculation instead.
     * This yielded different results in precisions.
     *
     * @param date
     * @param timezoneOffsetFromUtc
     * @param timePastLocalMidnight
     * @return
     */
    public double getJulianCentury(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // G column
        return (getJulianDay(date, timezoneOffsetFromUtc, timePastLocalMidnight) - JULIANDATE_FOR_EPOCHJ2000) / JULIAN_DAYS_PER_CENTURY;
    }

    /**
     * Get Julian day, which counts the number of days since noon on January 1, 4713 BC.
     *
     * The result returned from this method might be slightly different in precisions
     * from the result displayed in the Excel file (this class is based on).
     * <br/><br/>
     * There are two reasons such as:
     * <br/><br/>
     * First, in the Excel file, the value of each cell in Julian Day column
     * is formatted to display with only 2 decimal places (2 digits to the right of decimal point).
     * But, its value including all precisions is used instead
     * when another cell, referencing it, performs a calculation.
     * For example, its value displayed in a cell is 2460352.21 but 2460352.2125
     * is used instead when a cell in Julian Century column performs a calculation.
     * <br/><br/>
     * Second, Excel displays a number in 15 significant figures/digits,
     * but it uses all available digits or precisions while performing a calculation.
     * For example, its value displayed in the Excel file is 2460352.21666667,
     * but 2460352.216666667 (one more digit in decimal place) is used instead by
     * a cell in the Julian Century column while performing a calculation.
     *
     * @param date
     * @param timezoneOffsetFromUtc
     * @param timePastLocalMidnight
     * @return
     */
    public double getJulianDay(GregorianCalendar date, double timezoneOffsetFromUtc, double timePastLocalMidnight) { // F column
        // timezoneOffsetFromUtc must be double to get more digits in fractional part of a decimal number (a floating-point number in programming).
        return JULIANDATE_FOR_1900DEC30 + getNumOfDaysSince1900(date) + timePastLocalMidnight - timezoneOffsetFromUtc / 24;
    }

    /**
     * Get the number of days from January 1, 1900 to a given date.
     * For example, the number of days from January 1, 1900 to January 27, 2024 is 45318.
     * @param date
     * @return the number of days
     */
    public long getNumOfDaysSince1900(GregorianCalendar date) {
        final Calendar year1900 = new GregorianCalendar(1900,0,1);
        final long numOfDaysInMillis = date.getTimeInMillis() - year1900.getTimeInMillis();

        // 1). Excel stores dates as sequential serial numbers so that they can be used in calculations.
        // By default, January 1, 1900 is serial number 1, and January 1, 2008 is serial number 39448
        // because it is 39447 days after January 1, 1900.
        // 2). Excel thinks 1900 is a leap year (29 days in February) so the year had one more day.
        // THEREFOR, if a cell stores the date February 12, 2024, its value in number is 45334 (45332 days after January 1, 1900),
        // which is two days more than the result from any online calculators.
        // Since I'm using data in the excel file downloaded from https://gml.noaa.gov/grad/solcalc/ to verify,
        // I have to add two more days to the actual value.
        final int EXCEL_DIFF = 2;
        return EXCEL_DIFF + numOfDaysInMillis / 1000 / 60 / 60 / 24;
    }

}
