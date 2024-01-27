package com.vathanakmao.solarnoon;

import java.time.LocalTime;
import java.util.Date;

/**
 * Solar noon calculator
 */
public class SolarNoonCalc {

    /**
     * Get the local time of the specified date and location
     * that the sun is at its highest in the sky.
     *
     * @param lat - latitude of the location, for example, 11.566143 for Phnom Penh city, Cambodia.
     * @param lon - lnogitude of the location, for example, 104.935913 for Phnom Penh city, Cambodia.
     * @param timezoneOffiset - timezone offset from UTC. For example, if timezone is UTC+7, then it's 7.
     * @param date - the date for the solar noon
     * @return the time of the solar noon
     */
    public LocalTime getTime(long lat, long lon, int timezoneOffiset, Date date) {
        LocalTime result = null;

        double solarTime = (720 - 4 * lon - getEquationOfTime() + timezoneOffiset * 60) / 1440;

        return result;
    }

    private double getEquationOfTime() { // V column
        return 4 * Math.toDegrees(getVarY() * Math.sin(2 * Math.toRadians(getGeomMeanLongSun())) -2 * getEccentEarthOrbit() * Math.sin(Math.toRadians(getGeomMeanAnomSun())) + 4 * getEccentEarthOrbit() * getVarY() * Math.sin(Math.toRadians(getGeomMeanAnomSun())) * Math.cos(2 * Math.toRadians(getGeomMeanLongSun())) - 0.5 * getVarY() * getVarY() * Math.sin(4 * Math.toRadians(getGeomMeanLongSun())) - 1.25 * getEccentEarthOrbit() * getEccentEarthOrbit() * Math.sin(2 * Math.toRadians(getGeomMeanAnomSun())));
    }

    private double getVarY() { // U column
        return Math.tan(Math.toRadians(getObliqCorrInDegrees()/2)) * Math.tan(Math.toRadians(getObliqCorrInDegrees()/2));
    }

    private double getObliqCorrInDegrees() { // R column
        return getMeanObliqEclipticInDegrees() + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * getJulianCentury()));
    }

    private double getMeanObliqEclipticInDegrees() { // Q column
        return 23 + (26 + ((21.448 - getJulianCentury() * (46.815 + getJulianCentury() * (0.00059 - getJulianCentury() * 0.001813)))) / 60) / 60;
    }

    private float getJulianCentury() { // G column
        return (getJulianDay() - 2451545) / 36525;
    }

    private float getJulianDay() { // F column
        return date + 2415018.5 + E121 - $B$5 / 24;
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
