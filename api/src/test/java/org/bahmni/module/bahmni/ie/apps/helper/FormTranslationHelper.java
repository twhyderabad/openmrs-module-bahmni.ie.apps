package org.bahmni.module.bahmni.ie.apps.helper;

import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;

import java.util.HashMap;

public class FormTranslationHelper {
    public static FormTranslation createFormTranslation(String locale, String version, String formName) {
        FormTranslation formTranslation = new FormTranslation();
        formTranslation.setLocale(locale);
        formTranslation.setVersion(version);
        formTranslation.setFormName(formName);
        HashMap<String, String> concepts = new HashMap<>();
        concepts.put("TEMPERATURE_1", "Temperature");
        concepts.put("TEMPERATURE_1_DESC", "Temperature");
        formTranslation.setConcepts(concepts);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("LABEL_2", "Vitals");
        formTranslation.setLabels(labels);
        return formTranslation;
    }

}
