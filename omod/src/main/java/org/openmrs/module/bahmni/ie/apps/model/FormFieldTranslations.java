package org.openmrs.module.bahmni.ie.apps.model;

import java.util.ArrayList;
import java.util.Map;

public class FormFieldTranslations {
    private Map<String, ArrayList<String>> conceptsWithAllName;
    private Map<String, String> labelsWithAllName;
    private String locale;

    public Map<String, ArrayList<String>> getConceptsWithAllName() {
        return conceptsWithAllName;
    }

    public void setConceptsWithAllName(Map<String, ArrayList<String>> conceptsWithAllName) {
        this.conceptsWithAllName = conceptsWithAllName;
    }

    public Map<String, String> getLabelsWithAllName() {
        return labelsWithAllName;
    }

    public void setLabelsWithAllName(Map<String, String> labelsWithAllName) {
        this.labelsWithAllName = labelsWithAllName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
