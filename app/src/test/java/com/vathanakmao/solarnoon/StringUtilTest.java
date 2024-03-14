package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import com.vathanakmao.solarnoon.util.StringUtil;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void prependZeroIfOneDigit() {
        assertEquals("-100", StringUtil.prependZeroIfOneDigit(-100));
        assertEquals("-10", StringUtil.prependZeroIfOneDigit(-10));
        assertEquals("-09", StringUtil.prependZeroIfOneDigit(-9));
        assertEquals("00", StringUtil.prependZeroIfOneDigit(0));
        assertEquals("00", StringUtil.prependZeroIfOneDigit(-0));
        assertEquals("09", StringUtil.prependZeroIfOneDigit(9));
        assertEquals("10", StringUtil.prependZeroIfOneDigit(10));
        assertEquals("100", StringUtil.prependZeroIfOneDigit(100));
    }
}
