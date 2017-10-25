package org.openmrs.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmni.ie.apps.model.FormTranslation;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class BahmniFormTranslationServiceImplTest {


    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Context.class);
    }

    @Test
    public void shouldFetchTranslationsForGivenLocale() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        FormTranslation formTranslationFr = createFormTranslation("fr", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);
        bahmniFormTranslationService.saveFormTranslation(formTranslationFr);

        List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "fr");
        assertEquals(1, formTranslations.size());
        assertEquals("fr", formTranslations.get(0).getLocale());
    }

    @Test
    public void shouldFetchAllTranslationsIfNoLocaleGiven() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        FormTranslation formTranslationFr = createFormTranslation("fr", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);
        bahmniFormTranslationService.saveFormTranslation(formTranslationFr);

        List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", null);
        assertEquals(2, formTranslations.size());
    }

    @Test
    public void shouldThrowAPIExceptionIfTranslationFileIsNotPresentForGivenFormNameAndVersion() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        setTranslationPath(bahmniFormTranslationService, "/var/www/blah/blah");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Unable to find translation file for test_form_v1");
        bahmniFormTranslationService.getFormTranslations("test_form","1", "en");
    }

    @Test
    public void shouldSaveTranslationsOfGivenForm() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        String tempTranslationsPath = createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
        String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
        File translationFile = new File(tempTranslationsPath + "/test_form_1.json");
        assertTrue(translationFile.exists());
        assertEquals(FileUtils.readFileToString(translationFile), expected);
    }

    @Test
    public void shouldThrowAPIExceptionIfFormNameIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", "1", null);
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfItUnableToSaveTranslations() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        FormTranslation formTranslation = createFormTranslation("en", "1", "test_form");
        setTranslationPath(bahmniFormTranslationService, "/var/www/blah/blah");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("/test_form_1.json' could not be created");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfFormVersionIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", null, "test_form");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfLocaleIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation(null, "1", "test_form");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }


    private static FormTranslation createFormTranslation(String locale, String version, String formName) {
        FormTranslation formTranslation = new FormTranslation();
        formTranslation.setLocale(locale);
        formTranslation.setVersion(version);
        formTranslation.setFormName(formName);
        HashMap<String, String> concepts = new HashMap<>();
        concepts.put("TEMPERATURE_1", "Temperature");
        formTranslation.setConcepts(concepts);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("LABEL_2", "Vitals");
        formTranslation.setLabels(labels);
        return formTranslation;
    }

    private static String createTempFolder(BahmniFormTranslationService bahmniFormTranslationService) throws IOException, NoSuchFieldException, IllegalAccessException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        String translationsPath = temporaryFolder.getRoot().getAbsolutePath();
        return setTranslationPath(bahmniFormTranslationService, translationsPath);
    }

    private static String setTranslationPath(BahmniFormTranslationService bahmniFormTranslationService, String translationsPath) throws NoSuchFieldException, IllegalAccessException {
        Field field = bahmniFormTranslationService.getClass().getDeclaredField("FORM_TRANSLATIONS_PATH");
        field.setAccessible(true);
        field.set(bahmniFormTranslationService, translationsPath);
        return translationsPath;
    }
}