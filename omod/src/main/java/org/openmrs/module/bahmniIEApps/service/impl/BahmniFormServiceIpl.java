package org.openmrs.module.bahmniIEApps.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.APIException;
import org.openmrs.api.FormService;
import org.openmrs.module.bahmniIEApps.dao.BahmniFormDao;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;
import org.openmrs.module.bahmniIEApps.service.BahmniFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BahmniFormServiceIpl implements BahmniFormService {
    private FormService formService;
    private BahmniFormDao bahmniFormDao;
    private final String DEFAULT_VERSION = "1.0";
    private final String MULTIPLE_DRAFT_EXCEPTION = "Form cannot have more than one draft.";

    @Autowired
    public BahmniFormServiceIpl(FormService formService, BahmniFormDao bahmniFormDao) {
        this.formService = formService;
        this.bahmniFormDao = bahmniFormDao;
    }

    @Override
    @Transactional
    public FormResource saveFormResource(BahmniFormResource bahmniFormResource) {
        Form form = formService.getFormByUuid(bahmniFormResource.getForm().getFormUuid());
        FormResource formResource = getFormResource(bahmniFormResource.getFormResourceUuid());
        if(form.getPublished()) {
//            validateForMultileDraft(form.getName());
            form = cloneForm(form);
            form.setVersion(incrementVersion(form.getName()));
            formService.saveForm(form);

            formResource = cloneFormResource(formResource);
        }
        formResource.setForm(form);
        formResource.setName(bahmniFormResource.getName());
        formResource.setDatatypeClassname(bahmniFormResource.getDataType());
        formResource.setValueReferenceInternal(bahmniFormResource.getValueReference());
        formService.saveFormResource(formResource);
        return formResource;
    }

    @Override
    public Form publish(String formUuid) {
        Form form = formService.getFormByUuid(formUuid);
        if (form != null) {
            form.setPublished(Boolean.TRUE);
            formService.saveForm(form);
        }
        return form;
    }

    private FormResource getFormResource(String formResourceUuid) {
        FormResource formResource = formService.getFormResourceByUuid(formResourceUuid);
        if(null == formResource) {
            formResource = new FormResource();
        }
        return formResource;
    }

    private Form cloneForm(Form form) {
        Form clonedForm = new Form();
        clonedForm.setName(form.getName());
        clonedForm.setVersion(form.getVersion());
        clonedForm.setBuild(form.getBuild());
        clonedForm.setEncounterType(form.getEncounterType());
        clonedForm.setCreator(form.getCreator());
        return clonedForm;
    }

    private FormResource cloneFormResource(FormResource formResource) {
        FormResource clonedFormResource = new FormResource();
        clonedFormResource.setName(formResource.getName());
        clonedFormResource.setValueReferenceInternal(formResource.getValueReference());
        clonedFormResource.setDatatypeClassname(formResource.getDatatypeClassname());
        clonedFormResource.setDatatypeConfig(formResource.getDatatypeConfig());
        clonedFormResource.setPreferredHandlerClassname(formResource.getPreferredHandlerClassname());
        clonedFormResource.setHandlerConfig(formResource.getHandlerConfig());
        return clonedFormResource;
    }

    private void validateForMultileDraft(String formName) {
        List<Form> forms = bahmniFormDao.getDraftFormByName(formName);
        if(CollectionUtils.isNotEmpty(forms)) {
            throw new APIException(MULTIPLE_DRAFT_EXCEPTION);
        }
    }

    private String incrementVersion(String formName) {
        Form form = formService.getForm(formName);
        if(null != form) {
            Float version = Float.parseFloat(form.getVersion());
            version++;
            return version.toString();
        }
        return DEFAULT_VERSION;
    }
}
