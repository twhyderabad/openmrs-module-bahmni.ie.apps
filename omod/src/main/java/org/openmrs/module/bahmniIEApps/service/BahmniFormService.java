package org.openmrs.module.bahmniIEApps.service;

import org.openmrs.module.bahmniIEApps.model.BahmniForm;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;

import java.util.List;

public interface BahmniFormService {
    BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource);
    BahmniForm publish(String formUuid);
    List<BahmniForm> getAllLatestPublishedForms(boolean includeRetired, String encounterUuid);
}
