package com.vathanakmao.solarnoon.util;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    public static final String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
