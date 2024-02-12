package com.vathanakmao.solarnoon;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Solar noon calculator
 */
public class SolarNoonCalc {
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
     * @param lon - lnogitude of the location, for example, 104.935913 for Phnom Penh city, Cambodia.
     * @param timezoneOffsetFromUtc - timezone offset from UTC. For example, if timezone is UTC+7, then it's 7.
     * @param date - the date for the solar noon
     * @return the time of the solar noon
     */
    public LocalTime getTime(long lat, long lon, int timezoneOffsetFromUtc, GregorianCalendar date) {
        LocalTime result = null;

        double solarTime = (720 - 4 * lon - getEquationOfTime(date, timezoneOffsetFromUtc) + timezoneOffsetFromUtc * 60) / 1440;

        return result;
    }

    private double getEquationOfTime(GregorianCalendar date, int timezoneOffsetFromUtc) { // V column
        return 4 * Math.toDegrees(getVarY(date, timezoneOffsetFromUtc) * Math.sin(2 * Math.toRadians(getGeomMeanLongSun())) -2 * getEccentEarthOrbit() * Math.sin(Math.toRadians(getGeomMeanAnomSun())) + 4 * getEccentEarthOrbit() * getVarY(date, timezoneOffsetFromUtc) * Math.sin(Math.toRadians(getGeomMeanAnomSun())) * Math.cos(2 * Math.toRadians(getGeomMeanLongSun())) - 0.5 * getVarY(date, timezoneOffsetFromUtc) * getVarY(date, timezoneOffsetFromUtc) * Math.sin(4 * Math.toRadians(getGeomMeanLongSun())) - 1.25 * getEccentEarthOrbit() * getEccentEarthOrbit() * Math.sin(2 * Math.toRadians(getGeomMeanAnomSun())));
    }

    private double getVarY(GregorianCalendar date, int timezoneOffsetFromUtc) { // U column
        return Math.tan(Math.toRadians(getObliqCorrInDegrees(date, timezoneOffsetFromUtc)/2)) * Math.tan(Math.toRadians(getObliqCorrInDegrees(date, timezoneOffsetFromUtc)/2));
    }

    private double getObliqCorrInDegrees(GregorianCalendar date, int timezoneOffsetFromUtc) { // R column
        return getMeanObliqEclipticInDegrees(date, timezoneOffsetFromUtc) + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * getJulianCentury(date, timezoneOffsetFromUtc)));
    }

    private double getMeanObliqEclipticInDegrees(GregorianCalendar date, int timezoneOffsetFromUtc) { // Q column
        return 23 + (26 + ((21.448 - getJulianCentury(date, timezoneOffsetFromUtc) * (46.815 + getJulianCentury(date, timezoneOffsetFromUtc) * (0.00059 - getJulianCentury(date, timezoneOffsetFromUtc) * 0.001813)))) / 60) / 60;
    }

    /**
     * Get Julian century
     * @param date
     * @param timezoneOffsetFromUtc
     * @return
     */
    private double getJulianCentury(GregorianCalendar date, int timezoneOffsetFromUtc) { // G column
        return (getJulianDay(date, timezoneOffsetFromUtc) - 2451545) / 36525;
    }

    /**
     * Get Julian day number, which counts the number of days since noon on January 1, 4713 BC.
     *
     * @param date A given date
     * @param timezoneOffsetFromUtc E.g. Phnom Penh's timezone is UTC+7 then its value is 7.
     * @return Julian day number
     */
    public double getJulianDay(GregorianCalendar date, int timezoneOffsetFromUtc) { // F column
        // (45318 + 2415018.5) + (0.1/24) - (7/24)
        return getNumOfDaysSince1900(date) + 2415018.5 + getTimePastLocalMidnight() - timezoneOffsetFromUtc / 24;
    }

    /**
     * Get the number of days from January 1, 1900 to a given date.
     * For example, the number of days from January 1, 1900 to January 27, 2024 is 45318.
     * @param date
     * @return the number of days
     */
    public long getNumOfDaysSince1900(GregorianCalendar date) {
        Calendar year1900 = new GregorianCalendar(1900,0,1);
        long numOfDaysInMillis = date.getTimeInMillis() - year1900.getTimeInMillis();
        return numOfDaysInMillis / 1000 / 60 / 60 / 24;
    }

    /**
     * Suppose the local time past midnight is 00:06:00.
     * Then, the number of day past is 0.00416 (0.1/24) day.
     * @return
     */
    private float getTimePastLocalMidnight() {
        return 0.1F/24;
    }

    private long getGeomMeanLongSun() {
        throw new UnsupportedOperationException();
    }

    private long getEccentEarthOrbit() {
        throw new UnsupportedOperationException();
    }

    private long getGeomMeanAnomSun() {
        throw new UnsupportedOperationException();
    }

}
