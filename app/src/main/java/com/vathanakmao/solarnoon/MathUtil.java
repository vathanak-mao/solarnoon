package com.vathanakmao.solarnoon;


import android.health.connect.LocalTimeRangeFilter;
import android.icu.text.DecimalFormat;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Locale;

public class MathUtil {

    /**
     * Round the given number to 15 significant digits (figures).
     * As the calculations here are based on the Excel file,
     * which allows only 15 significant digits,
     * any results must be rounded.
     *
     * For example:
     *  + 0.0001234567890123456 will be rounded to 0.000123456789012345.
     *  + 1.000123456789012 will be rounded to 1.00012345678901.
     *
     * @param num
     * @return
     */
    public static double to15SignificantDigits(double num) {
        final String result = String.format(Locale.US, "%.15G", num);
        try {
            return Double.valueOf(result);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return num;
        }
    }

    public static double roundTo8DecimalPlaces(double num) {
        return round(num, 8);
    }

    public static double round(double number, int n) {
        final String result = String.format(Locale.US, "%."+n+"f", number);
        try {
            return Double.valueOf(result);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return number;
        }
    }

}
