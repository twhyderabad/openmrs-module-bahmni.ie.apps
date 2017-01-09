package org.openmrs.module.bahmni.ie.apps;

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.module.bahmni.ie.apps.model.BahmniFormResource;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;

public class MotherForm {

    public static FormResource createFormResource(Integer id, String valueReference, String dataType, String uuid, Form form) {
        return new FormResource() {{
            setId(id);
            setValueReferenceInternal(valueReference);
            setDatatypeClassname(dataType);
            setUuid(uuid);
            setForm(form);
        }};
    }

    public static Form createForm(String name, String uuid, String version, boolean isPublished) {
        return new Form() {{
            setName(name);
            setUuid(uuid);
            setVersion(version);
            setPublished(isPublished);
        }};
    }

    public static BahmniForm createBahmniForm(String formName, String uuid) {
        return new BahmniForm() {{
            setName(formName);
            setUuid(uuid);
        }};
    }

    public static BahmniFormResource createBahmniFormResource(String dataType, String uuid, String valueReference, BahmniForm bahmniForm) {
        return new BahmniFormResource() {{
            setDataType(dataType);
            setUuid(uuid);
            setValueReference(valueReference);
            setForm(bahmniForm);
        }};
    }
}
