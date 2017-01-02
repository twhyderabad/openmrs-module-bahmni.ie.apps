package org.openmrs.module.bahmniIEApps.model;

import org.openmrs.Obs;
import org.openmrs.module.bahmniIEApps.BahmniFormException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObsForm {

    public static String BAHMNI_FORM_NAMESPACE = "bahmni";

    public static Pattern FORM_FIELD_PATH_PATTERN = Pattern.compile("(.+)\\.(\\d+)/(\\d+)-(\\d+)");

    private String namespace;

    private String formName;

    private String formVersion;

    private String controlId;

    private String addMoreId;

    public ObsForm(Obs obs) throws BahmniFormException {
        this.namespace = obs.getFormFieldNamespace();
        if(obs.getFormFieldPath() != null && BAHMNI_FORM_NAMESPACE.equals(this.namespace)){
            Matcher matcher = FORM_FIELD_PATH_PATTERN.matcher(obs.getFormFieldPath());
            if(matcher.find()){
                this.formName = matcher.group(1);
                this.formVersion = matcher.group(2);
                this.controlId = matcher.group(3);
                this.addMoreId = matcher.group(4);
            }else{
                throw new BahmniFormException("obs with formfieldpath ["+obs.getFormFieldPath()+"] is not recognized");
            }
        }else{
            throw new BahmniFormException("obs with formnamespace ["+ namespace+"] is not recognized");
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFormName() {
        return formName;
    }

    public String getFormVersion() {
        return formVersion;
    }

    public String getControlId() {
        return controlId;
    }

    public String getAddMoreId() {
        return addMoreId;
    }
}
