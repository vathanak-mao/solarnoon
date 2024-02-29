package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class LocaleTest {

    @Test
    public void createLocaleBasedOnLanguageCode() {
        final Locale fr = new Locale("fr");
        assertEquals("French", fr.getDisplayName());
        assertEquals("français", fr.getDisplayName(fr));

        final Locale km = new Locale("km");
        assertEquals("Khmer", km.getDisplayName());
        assertEquals("ខ្មែរ", km.getDisplayName(km));
    }

    @Test
    public void createLocaleBasedOnLanguageName() {
        final Locale fr = new Locale("French");
        assertEquals("french", fr.getDisplayName());
        assertEquals("french", fr.getDisplayName(fr));
        assertEquals("french", fr.getLanguage());
        assertEquals("french", fr.getDisplayLanguage());
        assertEquals("french", fr.getDisplayLanguage(fr));
        assertEquals("", fr.getISO3Country());
    }
}
