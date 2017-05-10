package org.openmrs.module.bahmni.ie.apps.controller;

import org.openmrs.module.bahmni.ie.apps.model.BahmniFormResource;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormService;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BahmniFormController extends BaseRestController {
    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmniie/form";

    private BahmniFormService bahmniFormService;

    @Autowired
    public BahmniFormController(BahmniFormService bahmniFormService) {
        this.bahmniFormService = bahmniFormService;
    }

    @RequestMapping(value = baseUrl + "/publish", method = RequestMethod.POST )
    @ResponseBody
    public BahmniForm publish(@RequestParam("formUuid") String formUuid) {
        return bahmniFormService.publish(formUuid);
    }

    @RequestMapping(value = baseUrl + "/save", method = RequestMethod.POST )
    @ResponseBody
    public BahmniFormResource save(@RequestBody BahmniFormResource bahmniFormResource) {
        return bahmniFormService.saveFormResource(bahmniFormResource);
    }

    @RequestMapping(value = baseUrl + "/latestPublishedForms", method = RequestMethod.GET )
    @ResponseBody
    public List<BahmniForm> getLatestPublishedForms(@RequestParam(value = "includeRetired", defaultValue = "false") boolean includeRetired,
                                                    @RequestParam(value = "encounterUuid", required = false) String encounterUuid) {
        return bahmniFormService.getAllLatestPublishedForms(includeRetired, encounterUuid);
    }

    @RequestMapping(value = baseUrl + "/allForms", method = RequestMethod.GET )
    @ResponseBody
    public List<BahmniForm> getAllForms() {
        return bahmniFormService.getAllForms();
    }
}
