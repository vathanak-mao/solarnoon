package com.vathanakmao.solarnoon;

import org.junit.Test;

import static org.junit.Assert.*;

import com.vathanakmao.solarnoon.util.MathUtil;

import java.time.ZonedDateTime;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetTimezone() {
//        assertFalse(true);
        assertEquals(7, MathUtil.toHours(ZonedDateTime.now().getOffset().getTotalSeconds()), 0.01);
    }
}