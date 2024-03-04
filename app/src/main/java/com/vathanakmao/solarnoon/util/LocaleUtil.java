package com.vathanakmao.solarnoon.util;

import java.util.Locale;

public final class LocaleUtil {

    public static final String getDisplayName(String langCode, boolean translated) {
        Locale locale = new Locale(langCode);
        if (translated) {
            return locale.getDisplayName(locale);
        } else {
            return locale.getDisplayName();
        }
    }
}
