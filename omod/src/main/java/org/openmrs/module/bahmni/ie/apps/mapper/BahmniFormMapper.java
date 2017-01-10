package org.openmrs.module.bahmni.ie.apps.mapper;

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.module.bahmni.ie.apps.model.BahmniFormResource;

public class BahmniFormMapper {
    public BahmniFormResource map(FormResource formResource) {
        BahmniFormResource bahmniFormResource = new BahmniFormResource();
        bahmniFormResource.setValueReference(formResource.getValue().toString());
        bahmniFormResource.setUuid(formResource.getUuid());
        bahmniFormResource.setForm(map(formResource.getForm()));
        return bahmniFormResource;
    }

    public BahmniForm map(Form form) {
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setName(form.getName());
        bahmniForm.setUuid(form.getUuid());
        bahmniForm.setVersion(form.getVersion());
        bahmniForm.setPublished(form.getPublished());
        return bahmniForm;
    }
}
