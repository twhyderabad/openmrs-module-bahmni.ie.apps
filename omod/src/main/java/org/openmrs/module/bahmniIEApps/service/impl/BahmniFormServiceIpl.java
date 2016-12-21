package org.openmrs.module.bahmniIEApps.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.module.bahmniIEApps.service.BahmniFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BahmniFormServiceIpl implements BahmniFormService {
    private FormService formService;

    @Autowired
    public BahmniFormServiceIpl(FormService formService) {
        this.formService = formService;
    }

    @Override
    public Form publish(String formUuid) {
        Form form = formService.getFormByUuid(formUuid);
        if (form != null) {
            form.setVersion(incrementVersion(form.getVersion()));
            form.setPublished(Boolean.TRUE);
            formService.saveForm(form);
        }
        return form;
    }

    private String incrementVersion(String formVersion) {
        if (StringUtils.isEmpty(formVersion)) return "1.0";
        Float version = Float.parseFloat(formVersion);
        version++;
        return version.toString();
    }
}
