package org.openmrs.module.bahmniIEApps.mapper;

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;

public class BahmniFormMapper {
    public BahmniFormResource map(FormResource formResource) {
        BahmniFormResource bahmniFormResource = new BahmniFormResource();
        bahmniFormResource.setDataType(formResource.getDatatypeClassname());
        bahmniFormResource.setValueReference(formResource.getValueReference());
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
