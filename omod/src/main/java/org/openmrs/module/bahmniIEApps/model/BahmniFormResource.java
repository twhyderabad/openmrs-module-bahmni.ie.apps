package org.openmrs.module.bahmniIEApps.model;

import java.io.Serializable;

public class BahmniFormResource implements Serializable {
    private BahmniForm form;
    private String formResourceUuid;
    private String dataType;
    private String name;
    private String valueReference;

    public class BahmniForm {
        private String formUuid;

        public String getFormUuid() {
            return formUuid;
        }

        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }
    }

    public BahmniForm getForm() {
        return form;
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }

    public String getFormResourceUuid() {
        return formResourceUuid;
    }

    public void setFormResourceUuid(String formResourceUuid) {
        this.formResourceUuid = formResourceUuid;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueReference() {
        return valueReference;
    }

    public void setValueReference(String valueReference) {
        this.valueReference = valueReference;
    }
}
