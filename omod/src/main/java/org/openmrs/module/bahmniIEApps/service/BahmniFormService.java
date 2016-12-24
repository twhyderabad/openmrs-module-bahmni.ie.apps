package org.openmrs.module.bahmniIEApps.service;

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;

public interface BahmniFormService {
    FormResource saveFormResource(BahmniFormResource bahmniFormResource);
    Form publish(String formUuid);
}
