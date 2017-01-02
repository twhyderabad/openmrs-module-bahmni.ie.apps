package org.openmrs.module.bahmniIEApps.dao;

import org.openmrs.Form;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;

import java.util.List;

public interface BahmniFormDao {
    List<Form> getAllForms(String formName, boolean includeRetired, boolean includeDraftState);
    List<Form> getDraftFormByName(String name);
    List<Form> getAllPublishedForms(boolean includeRetired);
    List<BahmniForm> getLatestPublishedFormRevisions(List<String> formNamesToIgnore);
    List<Form> getFormDetails(List<BahmniForm> formNamesAndVersionList);
}
