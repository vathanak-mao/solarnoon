package com.vathanakmao.solarnoon.util;

public class StringUtil {

    public static final String prependZeroIfOneDigit(int number) {
        if (number <= -10 || number >= 10) {
            return String.valueOf(number);
        }

        final StringBuilder result = new StringBuilder();
        for (char ch :String.valueOf(number).toCharArray()) {
            if (Character.isDigit(ch)) {
                result.append("0").append(ch);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
