package org.openmrs.module.bahmniIEApps.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

public class BahmniFormServiceTest extends BaseContextSensitiveTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Verifies(
            value = "should throw APIException when obs is null",
            method = "saveObs(Obs,String)"
    )
    public void shouldReturnAPIExceptionWhenObsIsNull() {
        this.expectedException.expect(APIException.class);
        this.expectedException.expectMessage(Context.getMessageSourceService().getMessage("Obs.error.cannot.be.null"));
        ObsService os = Context.getObsService();
        Obs o = os.saveObs((Obs)null, "Null Obs");
    }

}
