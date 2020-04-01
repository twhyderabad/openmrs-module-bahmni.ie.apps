package org.bahmni.module.bahmni.ie.apps.service;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.ExportResponse;
import org.openmrs.api.OpenmrsService;

import java.util.List;

public interface BahmniFormService extends OpenmrsService {

    BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource);

    BahmniForm publish(String formUuid);

    List<BahmniForm> getAllLatestPublishedForms(boolean includeRetired, String encounterUuid);

    List<BahmniForm> getAllForms();

    ExportResponse getFormsByListOfUuids(List<String> formUuids);

    BahmniFormResource saveFormNameTranslation(BahmniFormResource bahmniFormResource, String referenceFormUuid);
}
