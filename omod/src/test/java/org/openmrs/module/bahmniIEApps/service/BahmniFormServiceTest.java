package org.openmrs.module.bahmniIEApps.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.FormService;
import org.openmrs.module.bahmniIEApps.MotherForm;
import org.openmrs.module.bahmniIEApps.dao.BahmniFormDao;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;
import org.openmrs.module.bahmniIEApps.model.BahmniFormResource;
import org.openmrs.module.bahmniIEApps.service.impl.BahmniFormServiceIpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BahmniFormServiceTest {

    @Mock
    private FormService formService;
    @Mock
    private BahmniFormDao bahmniFormDao;

    private BahmniFormService service;

    @Before
    public void setUp() {
        initMocks(this);
        service = new BahmniFormServiceIpl(formService, bahmniFormDao);
    }

    @Test
    public void shouldSaveFormWithoutCreatingNewFormIfItIsInDraftState() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("FormResourceDataType", "FormResourceUuid", "ValueReference", bahmniForm);

        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
        FormResource formResource = MotherForm.createFormResource(1, "ValueReference", "FormResourceDataType", "FormResourceUuid", form);

        when(formService.getFormByUuid(any(String.class))).thenReturn(form);
        when(formService.getFormResourceByUuid(any(String.class))).thenReturn(formResource);
        when(formService.saveFormResource(any(FormResource.class))).thenReturn(formResource);

        BahmniFormResource updatedBahmniFormResource = service.saveFormResource(bahmniFormResource);

        Assert.assertNotNull(updatedBahmniFormResource);
        Assert.assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
        Assert.assertEquals("FormResourceDataType", updatedBahmniFormResource.getDataType());
        Assert.assertEquals("ValueReference", updatedBahmniFormResource.getValueReference());

        Assert.assertNotNull(updatedBahmniFormResource.getForm());
        Assert.assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
        Assert.assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
        Assert.assertEquals("FormVersion", updatedBahmniFormResource.getForm().getVersion());
        Assert.assertFalse(updatedBahmniFormResource.getForm().isPublished());
    }

    @Test
    public void shouldCreateNewFormIfItIsInPublishedState() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("FormResourceDataType", "FormResourceUuid", "ValueReference", bahmniForm);

        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
        FormResource formResource = MotherForm.createFormResource(1, "ValueReference", "FormResourceDataType", "FormResourceUuid", form);

        when(formService.getFormByUuid(any(String.class))).thenReturn(form);
        when(formService.getFormResourceByUuid(any(String.class))).thenReturn(formResource);
        when(formService.saveFormResource(any(FormResource.class))).thenReturn(formResource);

        BahmniFormResource updatedBahmniFormResource = service.saveFormResource(bahmniFormResource);

        Assert.assertNotNull(updatedBahmniFormResource);
        Assert.assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
        Assert.assertEquals("FormResourceDataType", updatedBahmniFormResource.getDataType());
        Assert.assertEquals("ValueReference", updatedBahmniFormResource.getValueReference());

        Assert.assertNotNull(updatedBahmniFormResource.getForm());
        Assert.assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
        Assert.assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
        Assert.assertEquals("FormVersion", updatedBahmniFormResource.getForm().getVersion());
        Assert.assertTrue(updatedBahmniFormResource.getForm().isPublished());
    }

    @Test
    public void shouldPublishForm() {
        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
        when(formService.getFormByUuid(any(String.class))).thenReturn(form);

        BahmniForm updatedBahmniForm = service.publish("FormUuid");

        Assert.assertNotNull(updatedBahmniForm);
        Assert.assertEquals("FormName", updatedBahmniForm.getName());
        Assert.assertEquals("FormUuid", updatedBahmniForm.getUuid());
        Assert.assertEquals("FormVersion", updatedBahmniForm.getVersion());
        Assert.assertTrue(updatedBahmniForm.isPublished());
    }

    @Test
    public void shouldReturnAllLatestVersionOfPublishedForms() {
        Form form1 = MotherForm.createForm("FormName", "FormUuid1", "1", true);
        Form form2 = MotherForm.createForm("FormName", "FormUuid2", "2", true);
        Form form3 = MotherForm.createForm("FormName", "FormUuid3", "3", true);
        Form form4 = MotherForm.createForm("FormName", "FormUuid4", "4", true);
        when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));

        List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false);

        Assert.assertNotNull(bahmniForms);
        Assert.assertEquals(1, bahmniForms.size());
        Assert.assertEquals("FormName", bahmniForms.get(0).getName());
        Assert.assertEquals("FormUuid4", bahmniForms.get(0).getUuid());
        Assert.assertEquals("4", bahmniForms.get(0).getVersion());
        Assert.assertTrue(bahmniForms.get(0).isPublished());
    }
}
