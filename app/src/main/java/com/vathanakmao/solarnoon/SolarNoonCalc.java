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
        return result;
    }
}
