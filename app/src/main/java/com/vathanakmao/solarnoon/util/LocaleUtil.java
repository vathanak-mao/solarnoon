package com.vathanakmao.solarnoon.util;

import java.util.Locale;

public final class LocaleUtil {

    public static final String getDisplayName(String langCode) {
        Locale locale = new Locale(langCode);
        return locale.getDisplayName(locale); // return, for example, 'ខ្មែរ' instead of 'Khmer'
    }
}
