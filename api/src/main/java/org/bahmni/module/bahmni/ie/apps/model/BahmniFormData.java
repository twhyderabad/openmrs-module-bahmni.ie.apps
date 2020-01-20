package org.bahmni.module.bahmni.ie.apps.model;

import java.util.List;

public class BahmniFormData {
    private BahmniForm formJson;
    private List<FormTranslation> translations;

    public BahmniForm getFormJson() {
        return formJson;
    }

    public void setFormJson(BahmniForm formJson) {
        this.formJson = formJson;
    }

    public List<FormTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<FormTranslation> translations) {
        this.translations = translations;
    }
}
