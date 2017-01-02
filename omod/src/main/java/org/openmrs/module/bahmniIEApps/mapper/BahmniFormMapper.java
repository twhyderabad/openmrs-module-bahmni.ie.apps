package org.openmrs.module.bahmniIEApps.mapper;

import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.Obs;
import org.openmrs.module.bahmniIEApps.BahmniFormException;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;
import org.openmrs.module.bahmniIEApps.model.ObsForm;
import org.apache.commons.logging.Log;

public class BahmniFormMapper {
    protected Log log = LogFactory.getLog(getClass());

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

    public BahmniForm map(Obs obs){
        BahmniForm bahmniForm = null;

        try {
            ObsForm obsForm = new ObsForm(obs);
            bahmniForm = new BahmniForm(obsForm.getFormName(),obsForm.getFormVersion());

        } catch (BahmniFormException e) {
            log.error(e);
        }

        return bahmniForm;
    }
}
