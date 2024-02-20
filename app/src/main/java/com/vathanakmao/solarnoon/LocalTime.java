package com.vathanakmao.solarnoon;

public class LocalTime {
    public static final double MAX_TIME_IN_DOUBLE = 0.999999999999999; // 23:59:59
    public static final double MIN_TIME_IN_DOUBLE = 0.000000000000001; // 00:00:00

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

        // For example, the time is 12:14:29 PM.
        // Then, it's converted to number as 0.510060092502.
        final double hourInDouble = time * 24 / MAX_TIME_IN_DOUBLE; // hourInDouble is 12.241442220048
        hour = (int) hourInDouble; // hour is 12

        final double minuteInDouble = (hourInDouble % hour) * 60 / MAX_TIME_IN_DOUBLE; // minuteInDouble is 14.48653320288
        minute = (int) minuteInDouble; // minute is 14

        final double secondInDouble = (minuteInDouble % minute) * 60 / MAX_TIME_IN_DOUBLE; // secondInDouble is 29.1919921728
        second = (int) secondInDouble; // second is 29
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
