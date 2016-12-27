package org.openmrs.module.bahmniIEApps.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Form;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class FormValidatorTest {

    @Mock
    private FormService formService;

    @Before
    public void setUp() {
        mockStatic(Context.class);
        PowerMockito.when(Context.getFormService()).thenReturn(formService);
        List<Form> forms = Arrays.asList(getForm("form1", "1"), getForm("form2", "1"));
        PowerMockito.when(formService.getAllForms(false)).thenReturn(forms);
    }

    private Form getForm(String name, String version) {
        Form form = new Form();
        form.setName(name);
        form.setVersion(version);
        return form;
    }

    @Test
    public void shouldRejectIfFormWithSameNameAndVersionAlreadyExists() throws Exception {
        Form form = getForm("form1", "1");

        Errors errors = new BindException(form, "form");
        new BahmniFormValidator().validate(form, errors);

        ObjectError objectError = errors.getAllErrors().get(0);

        assertThat(errors.hasErrors(), is(true));
        assertThat(objectError.getCode(), is("Form with same name and version already exists"));
    }

    @Test
    public void shouldNotRejectIfFormNameAndVersionDoNoExist() throws Exception {
        Form form = getForm("form3", "1");

        Errors errors = new BindException(form, "form");
        new BahmniFormValidator().validate(form, errors);

        assertThat(errors.hasErrors(), is(false));
    }
}
