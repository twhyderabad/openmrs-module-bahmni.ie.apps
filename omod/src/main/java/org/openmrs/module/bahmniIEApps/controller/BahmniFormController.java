package org.openmrs.module.bahmniIEApps.controller;

import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;
import org.openmrs.module.bahmniIEApps.service.BahmniFormService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BahmniFormController extends BaseRestController {
    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmniIE";

    private BahmniFormService bahmniFormService;

    @Autowired
    public BahmniFormController(BahmniFormService bahmniFormService) {
        this.bahmniFormService = bahmniFormService;
    }

    @RequestMapping(value = baseUrl + "/publish", method = RequestMethod.GET )
    @ResponseBody
    public Form publish(@RequestParam("formUuid") String formUuid) {
        return bahmniFormService.publish(formUuid);
    }

    @RequestMapping(value = baseUrl + "/save", method = RequestMethod.POST )
//    @ResponseBody
    public void save(@RequestBody BahmniFormResource bahmniFormResource) {
        bahmniFormService.saveFormResource(bahmniFormResource);
    }
}
