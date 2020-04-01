package org.bahmni.module.bahmni.ie.apps.controller;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.ExportResponse;
import org.bahmni.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.bahmni.module.bahmni.ie.apps.model.FormNameTranslation;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class BahmniFormController extends BaseRestController {

    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmniie/form";

    private BahmniFormService bahmniFormService;

    private BahmniFormTranslationService bahmniFormTranslationService;

    @Autowired
    public BahmniFormController(BahmniFormService bahmniFormService,
                                BahmniFormTranslationService bahmniFormTranslationService) {
        this.bahmniFormService = bahmniFormService;
        this.bahmniFormTranslationService = bahmniFormTranslationService;
    }

    @RequestMapping(value = baseUrl + "/saveTranslation", method = RequestMethod.POST)
    @ResponseBody
    public List<FormTranslation> FormTranslation(@RequestBody List<FormTranslation> formTranslations) {
        return bahmniFormTranslationService.saveFormTranslation(formTranslations);
    }

    @RequestMapping(value = baseUrl + "/translations", method = RequestMethod.GET)
    @ResponseBody
    public List<FormTranslation> getTranslations(@RequestParam(value = "formName") String formName,
                                                 @RequestParam(value = "formVersion") String formVersion,
                                                 @RequestParam(value = "formUuid") String formUuid,
                                                 @RequestParam(value = "locale", required = false) String locale) {
        return bahmniFormTranslationService.getFormTranslations(formName, formVersion, locale, formUuid);
    }

    @RequestMapping(value = baseUrl + "/translate", method = RequestMethod.GET)
    @ResponseBody
    public FormFieldTranslations translate(@RequestParam(value = "formName") String formName,
                                           @RequestParam(value = "formVersion") String formVersion,
                                           @RequestParam(value = "formUuid") String formUuid,
                                           @RequestParam(value = "locale") String locale) {
        return bahmniFormTranslationService.setNewTranslationsForForm(locale, formName, formVersion, formUuid);
    }

    @RequestMapping(value = baseUrl + "/publish", method = RequestMethod.POST)
    @ResponseBody
    public BahmniForm publish(@RequestParam("formUuid") String formUuid) {
        return bahmniFormService.publish(formUuid);
    }

    @RequestMapping(value = baseUrl + "/save", method = RequestMethod.POST)
    @ResponseBody
    public BahmniFormResource save(@RequestBody BahmniFormResource bahmniFormResource) {
        return bahmniFormService.saveFormResource(bahmniFormResource);
    }

    @RequestMapping(value = baseUrl + "/latestPublishedForms", method = RequestMethod.GET)
    @ResponseBody
    public List<BahmniForm> getLatestPublishedForms(
            @RequestParam(value = "includeRetired", defaultValue = "false") boolean includeRetired,
            @RequestParam(value = "encounterUuid", required = false) String encounterUuid) {
        return bahmniFormService.getAllLatestPublishedForms(includeRetired, encounterUuid);
    }

    @RequestMapping(value = baseUrl + "/allForms", method = RequestMethod.GET)
    @ResponseBody
    public List<BahmniForm> getAllForms() {
        return bahmniFormService.getAllForms();
    }

    @RequestMapping(value = baseUrl + "/export", method = RequestMethod.GET)
    @ResponseBody
    public ExportResponse export(@RequestParam ("uuid") List<String> uuids) {
        return bahmniFormService.getFormsByListOfUuids(uuids);
    }

    @RequestMapping(value = baseUrl + "/name/saveTranslation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<BahmniFormResource> saveFormNameTranslations(@RequestBody BahmniFormResource bahmniFormResource, @RequestParam(value = "referenceFormUuid", required = false) String referenceFormUuid) {
        try {
            return new ResponseEntity<>(bahmniFormService.saveFormNameTranslation(bahmniFormResource, referenceFormUuid), HttpStatus.OK);
        } catch (APIException ae) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(value = baseUrl + "/name/translate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getFormNameTranslations(@RequestParam(value = "formName") String formName, @RequestParam(value = "formUuid") String formUuid) {
        String response =bahmniFormTranslationService.getFormNameTranslations(formName, formUuid);
        if (response != null)
            return new ResponseEntity<>(response, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
