package com.vathanakmao.solarnoon;


import android.icu.text.DecimalFormat;

import java.math.BigDecimal;
import java.util.Locale;

public class MathUtil {

    /**
     * Round the given number to 15 significant digits.
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
        final String result = String.format(Locale.US, "%."+15+"G", num);
        try {
            return Double.valueOf(result);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return num;
        }
    }
}
