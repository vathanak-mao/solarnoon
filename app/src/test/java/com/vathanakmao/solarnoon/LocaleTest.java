package com.vathanakmao.solarnoon;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        final Locale fr = Locale.forLanguageTag("English");
        assertEquals("english", fr.getLanguage());

        assertEquals("english", fr.getDisplayName());
        assertEquals("english", fr.getDisplayName(fr));
        assertEquals("english", fr.getLanguage());
        assertEquals("english", fr.getDisplayLanguage());
        assertEquals("english", fr.getDisplayLanguage(fr));
        assertEquals("", fr.getISO3Country());
    }
}
