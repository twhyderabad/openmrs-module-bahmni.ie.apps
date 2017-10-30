package org.openmrs.module.bahmni.ie.apps.model;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormTranslationTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void shouldCreateFormTranslationFromGivenJsonObject() throws Exception {
        String json = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature Desc\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
        JSONObject translation = new JSONObject(json);

        FormTranslation formTranslation = FormTranslation.parse(translation, "en");

        assertEquals("en", formTranslation.getLocale());
        assertEquals(2, formTranslation.getConcepts().size());
        assertEquals(1, formTranslation.getLabels().size());
        assertEquals("Temperature", formTranslation.getConcepts().get("TEMPERATURE_1"));
        assertEquals("Temperature Desc", formTranslation.getConcepts().get("TEMPERATURE_1_DESC"));
        assertEquals("Vitals", formTranslation.getLabels().get("LABEL_2"));
    }

    @Test
    public void shouldReturnEmptyFormTranslationObjectForUnknownLocale() throws Exception {
        String json = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature Desc\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
        JSONObject translation = new JSONObject(json);

        FormTranslation formTranslation = FormTranslation.parse(translation, "pt");

        assertEquals(null, formTranslation.getLabels());
        assertEquals(null, formTranslation.getConcepts());
        assertEquals(null, formTranslation.getLocale());
    }
}