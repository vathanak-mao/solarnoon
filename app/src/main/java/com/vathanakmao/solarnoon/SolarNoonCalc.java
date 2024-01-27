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

    private double getEquationOfTime() {
        return 4 * Math.toDegrees(getVarY() * Math.sin(2 * Math.toRadians(getGeomMeanLongSun())) -2 * getEccentEarthOrbit() * Math.sin(Math.toRadians(getGeomMeanAnomSun())) + 4 * getEccentEarthOrbit() * getVarY() * Math.sin(Math.toRadians(getGeomMeanAnomSun())) * Math.cos(2 * Math.toRadians(getGeomMeanLongSun())) - 0.5 * getVarY() * getVarY() * Math.sin(4 * Math.toRadians(getGeomMeanLongSun())) - 1.25 * getEccentEarthOrbit() * getEccentEarthOrbit() * Math.sin(2 * Math.toRadians(getGeomMeanAnomSun())));
    }

    private long getVarY() {
        throw new UnsupportedOperationException();
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
