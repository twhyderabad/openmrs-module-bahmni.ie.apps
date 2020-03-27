package org.bahmni.customdatatype.datatype;

import org.openmrs.customdatatype.SerializingCustomDatatype;

public class FormNameTranslationDatatype extends SerializingCustomDatatype<String> {

    @Override
    public String serialize(String typedValue) {
        return typedValue;
    }

    @Override
    public String deserialize(String serializedValue) {
        return serializedValue;
    }
}
