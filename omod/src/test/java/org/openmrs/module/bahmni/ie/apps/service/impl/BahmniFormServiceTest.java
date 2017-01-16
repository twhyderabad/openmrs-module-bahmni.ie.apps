package org.openmrs.module.bahmni.ie.apps.service.impl;

import org.junit.Test;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class BahmniFormServiceTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BahmniFormService bahmniFormService;

    @Test
    public void ensureThatPublishingAFormUpdatesTheValueReference() throws Exception {
        executeDataSet("formDataSet.xml");
        BahmniForm bahmniForm = bahmniFormService.publish("d9218f76-6c39-45f4-8efa-4c5c6c199f52");
        assertEquals("5",bahmniForm.getVersion());
        assertEquals(true,Context.getFormService().getForm(2).getPublished());

        FormResource formResource = Context.getFormService().getFormResource(1);
        assertEquals("src/test/resources/Bahmni_Form_5.json",formResource.getValueReference());
    }
}
