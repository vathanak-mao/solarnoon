package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.sql.Time;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SolarNoonCalcTest {
    public SolarNoonCalc calc = new SolarNoonCalc();

    @Test
    public void testGetTime() {
        int[] years = new int[]{ 2010 };
        Long[] latLonZoneOffsetList = new Long[] {40L, -105L, 7L}; // latitude, longitude, offsetFromUtc ("-7" means UTC-7)
        LocalTime solarNoonTime = calc.getTime(latLonZoneOffsetList[0], latLonZoneOffsetList[1], latLonZoneOffsetList[2].intValue(), years[0], LocalTime.NOON);

        assertTrue(solarNoonTime.getHour() == 12 && solarNoonTime.getMinute() == 3);
    }
}
