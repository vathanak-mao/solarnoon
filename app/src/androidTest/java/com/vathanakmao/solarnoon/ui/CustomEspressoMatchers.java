package com.vathanakmao.solarnoon.ui;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class CustomEspressoMatchers {

    public static Matcher<String> containsOnlyChars(char... expectedValues) {
        return new TypeSafeMatcher<String>() {

            @Override
            protected boolean matchesSafely(String actualValue) {
                for (char ac : actualValue.toCharArray()) {
                    boolean exists = false;
                    for (char ex : expectedValues) {
                        if (ac == ex) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
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
