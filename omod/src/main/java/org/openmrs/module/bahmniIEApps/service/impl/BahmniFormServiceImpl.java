package org.openmrs.module.bahmniIEApps.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniIEApps.dao.BahmniFormDao;
import org.openmrs.module.bahmniIEApps.mapper.BahmniFormMapper;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;
import org.openmrs.module.bahmniIEApps.service.BahmniFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BahmniFormServiceImpl implements BahmniFormService {
    private FormService formService;
    private BahmniFormDao bahmniFormDao;
    private final Integer DEFAULT_VERSION = 1;
    private final String MULTIPLE_DRAFT_EXCEPTION = "Form cannot have more than one drafts.";

    @Autowired
    public BahmniFormServiceImpl(FormService formService, BahmniFormDao bahmniFormDao) {
        this.formService = formService;
        this.bahmniFormDao = bahmniFormDao;
    }

    @Override
    @Transactional
    public BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource) {
        Form form = formService.getFormByUuid(bahmniFormResource.getForm().getUuid());
        FormResource formResource = getFormResource(bahmniFormResource.getUuid());
        if (form.getPublished()) {
            form = cloneForm(form);
            form.setVersion(nextGreatestVersionId(form.getName()).toString());
            formService.saveForm(form);

            formResource = cloneFormResource(formResource);
        }
        formResource.setForm(form);
        formResource.setName(bahmniFormResource.getForm().getName());
        formResource.setDatatypeClassname(bahmniFormResource.getDataType());
        formResource.setValueReferenceInternal(bahmniFormResource.getValueReference());
        formResource = formService.saveFormResource(formResource);
        return new BahmniFormMapper().map(formResource);
    }

    @Override
    public BahmniForm publish(String formUuid) {
        Form form = formService.getFormByUuid(formUuid);
        if (form != null) {
            Integer nextVersionNumber = nextGreatestVersionId(form.getName());
            if(Integer.parseInt(form.getVersion())+1 != nextVersionNumber) {
                form.setVersion(nextVersionNumber.toString());
            }
            form.setPublished(Boolean.TRUE);
            formService.saveForm(form);
        }
        return new BahmniFormMapper().map(form);
    }

    @Override
    public List<BahmniForm> getAllLatestPublishedForms(boolean includeRetired, String encounterUuid) {
        List<Form> allPublishedForms = bahmniFormDao.getAllPublishedForms(includeRetired);
        List<BahmniForm> latestPublishedForms = getLatestFormByVersion(allPublishedForms);

        if (encounterUuid == null) {
            return latestPublishedForms;
        }

        Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
        Set<Obs> obs = encounter.getAllObs(false);
        if (obs.isEmpty()) {
            return latestPublishedForms;
        }

        return getAllLatestPublishedForms(obs);
    }

    private List<Form> getAllLatestPublishedForms(Set<Obs> obsList){
        BahmniFormMapper formMapper = new BahmniFormMapper();

        List<String> formsToIgnore = new ArrayList<>();
        List<BahmniForm> obsForms = new ArrayList<>();
        Iterator<Obs> iterator = obsList.iterator();
        while(iterator.hasNext()){
            BahmniForm form = formMapper.map(iterator.next());
            if(form != null){
                obsForms.add(form);
                formsToIgnore.add(form.getName());
            }
        }
        List<BahmniForm> latestPublishedFormRevisions = bahmniFormDao.getLatestPublishedFormRevisions(formsToIgnore);

        latestPublishedFormRevisions.addAll(obsForms);

        return bahmniFormDao.getFormDetails(latestPublishedFormRevisions);
    }

    private List<BahmniForm> mergeForms(List<Form> allPublishedForms, List<BahmniForm> latestPublishedForms,
                                        Map<String, List<Obs>> groupedObsByFormName) {
        for (String formName : groupedObsByFormName.keySet()) {
            String[] formNameAndVersion = formName.split("\\.");
            boolean isSameVersion = latestPublishedForms.parallelStream()
                    .anyMatch(isSameBahmniForm(formNameAndVersion));
            if (!isSameVersion) {
                Form publishedFormWithObs = allPublishedForms.stream().filter(isSameForm(formNameAndVersion)).collect(Collectors.toList()).get(0);
                latestPublishedForms = latestPublishedForms.parallelStream().map(form -> {
                    if (form.getName().equals(formNameAndVersion[0])) {
                        form.setVersion(publishedFormWithObs.getVersion());
                        form.setUuid(publishedFormWithObs.getUuid());
                    }
                    return form;
                }).collect(Collectors.toList());
            }
        }
        return latestPublishedForms;
    }

    private Predicate<Form> isSameForm(String[] formNameAndVersion) {
        return form -> form.getName().equals(formNameAndVersion[0]) && form.getVersion().equals(formNameAndVersion[1]);
    }

    private Predicate<BahmniForm> isSameBahmniForm(String[] formNameAndVersion) {
        return form -> form.getName().equals(formNameAndVersion[0]) && form.getVersion().equals(formNameAndVersion[1]);
    }

    private static String getKey(Obs o) {
        return o.getFormFieldPath().split("/")[0];
    }

    private List<BahmniForm> getLatestFormByVersion(List<Form> forms) {
        Map<String, Form> bahmniFormMap = new LinkedHashMap<>();
        if(CollectionUtils.isNotEmpty(forms)) {
            for (Form form : forms) {
                String formName = form.getName();
                if (bahmniFormMap.containsKey(formName)) {
                    if (Integer.parseInt(form.getVersion()) > Integer.parseInt(bahmniFormMap.get(formName).getVersion())) {
                        bahmniFormMap.put(formName, form);
                    }
                } else {
                    bahmniFormMap.put(formName, form);
                }
            }
        }
        return map(bahmniFormMap);
    }

    private List<BahmniForm> map(Map<String, Form> forDetailsMap) {
        List<BahmniForm> bahmniForms = new ArrayList<>();
        BahmniFormMapper mapper = new BahmniFormMapper();
        if (MapUtils.isNotEmpty(forDetailsMap)) {
            for (Form form : forDetailsMap.values()) {
                bahmniForms.add(mapper.map(form));
            }
        }
        return bahmniForms;
    }

    private FormResource getFormResource(String formResourceUuid) {
        FormResource formResource = formService.getFormResourceByUuid(formResourceUuid);
        if (null == formResource) {
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
        if(null != formResource.getId()) {
            clonedFormResource.setName(formResource.getName());
            clonedFormResource.setValueReferenceInternal(formResource.getValueReference());
            clonedFormResource.setDatatypeClassname(formResource.getDatatypeClassname());
            clonedFormResource.setDatatypeConfig(formResource.getDatatypeConfig());
            clonedFormResource.setPreferredHandlerClassname(formResource.getPreferredHandlerClassname());
            clonedFormResource.setHandlerConfig(formResource.getHandlerConfig());
        }
        return clonedFormResource;
    }

    /**
     * This will throw APIException.class exception if there is more than one draft version.
     *
     * @param formName
     */
    private void validateForMultipleDraft(String formName) {
        List<Form> forms = bahmniFormDao.getDraftFormByName(formName);
        if (CollectionUtils.isNotEmpty(forms) && forms.size() > 1) {
            throw new APIException(MULTIPLE_DRAFT_EXCEPTION);
        }
    }

    private Integer nextGreatestVersionId(String formName) {
        List<Form> forms = bahmniFormDao.getAllForms(formName, false, true);
        float version = 0f;
        if (CollectionUtils.isNotEmpty(forms)) {
            for (Form form : forms) {
                float formVersion = Float.parseFloat(form.getVersion());
                if (formVersion > version) {
                    version = formVersion;
                }
            }
        }
        if (version > 0f) {
            version++;
            return (int) version;
        }
        return DEFAULT_VERSION;
    }
}
