package org.openmrs.module.bahmni.ie.apps.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bahmni.ie.apps.model.FormTranslation;

import java.util.List;


public interface BahmniFormTranslationService extends OpenmrsService {
    List<FormTranslation> getFormTranslations(String formName, String formVersion, String locale);
    FormTranslation saveFormTranslation(FormTranslation formTranslation);
}
