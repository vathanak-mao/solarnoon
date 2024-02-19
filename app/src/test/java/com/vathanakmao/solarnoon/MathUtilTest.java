package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import android.health.connect.LocalTimeRangeFilter;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilTest {
    public static final double ASSERTEQUAlS_DOUBLE_DELTA = 0.00000000000000001;

    @Test
    public void testTo15SignificantDigits() {
        assertEquals(0, Double.valueOf(7 / 24), ASSERTEQUAlS_DOUBLE_DELTA);

        assertEquals(0.291666666666667, MathUtil.to15SignificantDigits(7D / 24), ASSERTEQUAlS_DOUBLE_DELTA);
        assertEquals(2, MathUtil.to15SignificantDigits(4D / 2), ASSERTEQUAlS_DOUBLE_DELTA);
    }

    @Test
    public void testRoundTo8DecimalPlaces() {
        assertEquals(0.24112834, MathUtil.roundTo8DecimalPlaces(0.241128336755644), ASSERTEQUAlS_DOUBLE_DELTA);
        assertEquals(0.00000123, MathUtil.roundTo8DecimalPlaces(0.0000012345), ASSERTEQUAlS_DOUBLE_DELTA);
        assertEquals(1.12345679, MathUtil.roundTo8DecimalPlaces(1.123456789), ASSERTEQUAlS_DOUBLE_DELTA);
        assertEquals(12345678.12345679, MathUtil.roundTo8DecimalPlaces(12345678.123456789), ASSERTEQUAlS_DOUBLE_DELTA);
    }

}
