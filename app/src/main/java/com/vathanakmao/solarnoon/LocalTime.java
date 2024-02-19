package com.vathanakmao.solarnoon;

public class LocalTime {
    public static final double MAX_TIME_IN_DOUBLE = 0.999999999999999;
    public static final double MIN_TIME_IN_DOUBLE = 0.000000000000001;

    private int hour;
    private int minute;
    private int second;

    /**
     * In Excel, the cell value in Time, for example 12:14:29, can be converted to
     * Number as 0.510060092502, by changing the format of the cell from Time to Number.
     * @param time
     */
    public LocalTime(double time) {
        if (time < MIN_TIME_IN_DOUBLE || time > MAX_TIME_IN_DOUBLE) {
            throw new IllegalArgumentException(String.format("The parameter time, %s, cannot be less than %s and greater than %s.", time, MIN_TIME_IN_DOUBLE, MAX_TIME_IN_DOUBLE));
        }

        // 0.000000000000001 is converted to time as 12:00:00 AM (00:00:00).
        // 0.999999999999999 is converted to time as 12:59:59 PM (23:59:59).
        final double hourInDouble = time * 24 / MAX_TIME_IN_DOUBLE;
        hour = (int) hourInDouble;

        final double minuteInDouble = (hourInDouble % hour) * 60 / MAX_TIME_IN_DOUBLE;
        minute = (int) minuteInDouble;

        second = (int) ((minuteInDouble % minute) * 60 / MAX_TIME_IN_DOUBLE);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
