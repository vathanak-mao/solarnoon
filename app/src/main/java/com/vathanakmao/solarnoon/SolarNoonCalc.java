package com.vathanakmao.solarnoon;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>Solar noon calculator.<br/></p>
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

    private static SolarNoonCalc instance;

    protected SolarNoonCalc() {}

    public static SolarNoonCalc getInstance() {
        if (instance == null) {
            instance = new SolarNoonCalc();
        }
        return instance;
    }

    /**
     * Get the local time of the specified date and location
     * that the sun is at its highest in the sky.
     *
     * @param lat - latitude of the location, for example, 11.566143 for Phnom Penh city, Cambodia.
     * @param lon - longitude of the location, for example, 104.935913 for Phnom Penh city, Cambodia.
     * @param timezoneOffsetFromUtc - timezone offset from UTC. For example, if timezone is UTC+7, then it's 7.
     * @param date - the date for the solar noon
     * @return the time of the solar noon
     */
    public LocalTime getTime(long lat, long lon, double timezoneOffsetFromUtc, GregorianCalendar date) {
        LocalTime result = null;

        double solarTime = (720 - 4 * lon - getEquationOfTime(date, timezoneOffsetFromUtc) + timezoneOffsetFromUtc * 60) / 1440;

        return result;
    }

    private double getEquationOfTime(GregorianCalendar date, double timezoneOffsetFromUtc) { // V column
        final double geomMeanLongSun = getGeomMeanLongSun(date, timezoneOffsetFromUtc);
        final double geomMeanAnomSun = getGeomMeanAnomSun(date, timezoneOffsetFromUtc);
        return 4 * Math.toDegrees(getVarY(date, timezoneOffsetFromUtc) * Math.sin(2 * Math.toRadians(geomMeanLongSun)) -2 * getEccentEarthOrbit() * Math.sin(Math.toRadians(geomMeanAnomSun)) + 4 * getEccentEarthOrbit() * getVarY(date, timezoneOffsetFromUtc) * Math.sin(Math.toRadians(geomMeanAnomSun)) * Math.cos(2 * Math.toRadians(geomMeanLongSun)) - 0.5 * getVarY(date, timezoneOffsetFromUtc) * getVarY(date, timezoneOffsetFromUtc) * Math.sin(4 * Math.toRadians(geomMeanAnomSun)) - 1.25 * getEccentEarthOrbit() * getEccentEarthOrbit() * Math.sin(2 * Math.toRadians(geomMeanAnomSun)));
    }

    private double getVarY(GregorianCalendar date, double timezoneOffsetFromUtc) { // U column
        return Math.tan(Math.toRadians(getObliqCorrInDegrees(date, timezoneOffsetFromUtc)/2)) * Math.tan(Math.toRadians(getObliqCorrInDegrees(date, timezoneOffsetFromUtc)/2));
    }

    private double getObliqCorrInDegrees(GregorianCalendar date, double timezoneOffsetFromUtc) { // R column
        return getMeanObliqEclipticInDegrees(date, timezoneOffsetFromUtc) + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * getJulianCentury(date, timezoneOffsetFromUtc)));
    }

    private double getMeanObliqEclipticInDegrees(GregorianCalendar date, double timezoneOffsetFromUtc) { // Q column
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc);
        return 23 + (26 + ((21.448 - julianCentury * (46.815 + julianCentury * (0.00059 - julianCentury * 0.001813)))) / 60) / 60;
    }

    /**
     * Get the number of Julian centuries since the epoch J2000.
     * Check this class's documentation for what epoch J2000 is.
     *
     * @param date
     * @param timezoneOffsetFromUtc
     * @return
     */
    public double getJulianCentury(GregorianCalendar date, double timezoneOffsetFromUtc) { // G column
        return (getJulianDay(date, timezoneOffsetFromUtc) - JULIANDATE_FOR_EPOCHJ2000) / JULIAN_DAYS_PER_CENTURY;
    }

    /**
     * Get Julian day number, which counts the number of days since noon on January 1, 4713 BC.
     *
     * @param date A given date
     * @param timezoneOffsetFromUtc E.g. Phnom Penh's timezone is UTC+7 then its value is 7.
     * @return Julian day number
     */
    public double getJulianDay(GregorianCalendar date, double timezoneOffsetFromUtc) { // F column
        // timezoneOffsetFromUtc must be double to get more digits in fractional part of a decimal number (a floating-point number in programming).
        return JULIANDATE_FOR_1900DEC30 + getNumOfDaysSince1900(date) + getTimePastLocalMidnight() - timezoneOffsetFromUtc / 24;
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

    /**
     * Suppose the local time past midnight is 00:06:00.
     * Then, the number of day past is 0.00416 (0.1/24) day.
     * @return
     */
    public float getTimePastLocalMidnight() {
        return 0.1F/24;
    }

    private double getGeomMeanLongSun(GregorianCalendar date, double timezoneOffsetFromUtc) { // column I
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc);
        return (280.46646 + julianCentury * (36000.76983 + julianCentury * 0.0003032)) % 360;
    }

    private long getEccentEarthOrbit() {
        throw new UnsupportedOperationException();
    }

    private double getGeomMeanAnomSun(GregorianCalendar date, double timezoneOffsetFromUtc) {
        final double julianCentury = getJulianCentury(date, timezoneOffsetFromUtc);
        return 357.52911 + julianCentury * (35999.05029 - 0.0001537 * julianCentury);
    }

}
