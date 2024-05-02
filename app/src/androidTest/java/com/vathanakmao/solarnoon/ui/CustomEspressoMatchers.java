package com.vathanakmao.solarnoon.ui;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;

public final class CustomEspressoMatchers {

    public static Matcher<String> containsOnlyCharsIn(char... expectedValues) {
        return new TypeSafeMatcher<String>() {

            @Override
            protected boolean matchesSafely(String actualValue) {
                final List expectedList = Arrays.asList(expectedValues);
                for (char ch : actualValue.toCharArray()) {
                    if (!expectedList.contains(ch)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contains only characters in: " + expectedValues);
            }
        };
    }
}
