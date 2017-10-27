package org.openmrs.module.bahmni.ie.apps.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormFieldTranslations {
    private Map<String, ArrayList<String>> concepts;
    private Map<String, String> labels;
    private String locale;

    public FormFieldTranslations(Map<String, ArrayList<String>> concepts, Map<String, String> labels, String locale) {
        this.concepts = concepts;
        this.labels = labels;
        this.locale = locale;
    }

    public FormFieldTranslations() {
    }

    public Map<String, ArrayList<String>> getConcepts() {
        return concepts;
    }

    public void setConcepts(Map<String, ArrayList<String>> concepts) {
        this.concepts = concepts;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
