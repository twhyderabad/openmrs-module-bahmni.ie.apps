package org.bahmni.module.bahmni.ie.apps.service;

import org.bahmni.module.bahmni.ie.apps.model.FormNameTranslation;
import org.openmrs.api.OpenmrsService;
import org.bahmni.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;

import java.util.List;

public interface BahmniFormTranslationService extends OpenmrsService {

	List<FormTranslation> getFormTranslations(String formName, String formVersion, String locale, String formUuid);

	List<FormTranslation> saveFormTranslation(List<FormTranslation> formTranslation);

	FormFieldTranslations setNewTranslationsForForm(String locale, String formName, String version, String formUuid);

	String getFormNameTranslations(String formName, String uuid);
}
