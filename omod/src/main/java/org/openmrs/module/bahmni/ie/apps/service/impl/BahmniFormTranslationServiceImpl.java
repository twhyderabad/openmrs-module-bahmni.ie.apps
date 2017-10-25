package org.openmrs.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bahmni.ie.apps.model.FormTranslation;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class BahmniFormTranslationServiceImpl extends BaseOpenmrsService implements BahmniFormTranslationService {


    private static String FORM_TRANSLATIONS_PATH = "/var/www/bahmni_config/openmrs/apps/forms/translations/";

    @Override
    public List<FormTranslation> getFormTranslations(String formName, String formVersion, String locale) {
        JSONObject jsonObject = getTranslationJsonFromFile(formName, formVersion);
        return mapTranslations(jsonObject, locale, formName, formVersion);
    }

    @Override
    public FormTranslation saveFormTranslation(FormTranslation formTranslation) {
        if (!validate(formTranslation)) {
            throw new APIException("Invalid Parameters");
        }
        String formName = formTranslation.getFormName();
        String version = formTranslation.getVersion();
        File translationFile = new File(String.format("%s/%s_%s.json", FORM_TRANSLATIONS_PATH, formName, version));
        translationFile.getParentFile().mkdirs();
        saveTranslationsToFile(formTranslation, translationFile);

        return formTranslation;
    }

    private void saveTranslationsToFile(FormTranslation formTranslation, File translationFile) {
        try {
            String fileContent = translationFile.exists() ? FileUtils.readFileToString(translationFile) : "";
            JSONObject translations = new JSONObject();
            translations.put("labels", new JSONObject(formTranslation.getLabels()));
            translations.put("concepts", new JSONObject(formTranslation.getConcepts()));

            JSONObject translationsJson = isNotEmpty(fileContent) ? new JSONObject(fileContent) : new JSONObject();
            translationsJson.put(formTranslation.getLocale(), translations);

            FileUtils.writeStringToFile(translationFile, translationsJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new APIException(e.getMessage(), e);
        }
    }

    private boolean validate(FormTranslation formTranslation) {
        return isNotEmpty(formTranslation.getFormName()) &&
                isNotEmpty(formTranslation.getLocale()) &&
                isNotEmpty(formTranslation.getVersion());
    }

    private List<FormTranslation> mapTranslations(JSONObject jsonObject, String locale, String formName, String formVersion) {
        List<FormTranslation> translationsList = new ArrayList<>();
        if (isNotEmpty(locale))
            translationsList.add(getParsedTranslations(jsonObject, locale, formName, formVersion));
        else
            for (String key : jsonObject.keySet())
                translationsList.add(getParsedTranslations(jsonObject, key, formName, formVersion));
        return translationsList;
    }

    private FormTranslation getParsedTranslations(JSONObject jsonObject, String locale, String formName, String formVersion) {
        FormTranslation formTranslation = FormTranslation.parse(jsonObject, locale);
        formTranslation.setFormName(formName);
        formTranslation.setVersion(formVersion);
        return formTranslation;
    }


    private JSONObject getTranslationJsonFromFile(String formName, String formVersion) {
        JSONObject jsonObject = null;
        try {
            File translationFile = new File(String.format("%s/%s_%s.json", FORM_TRANSLATIONS_PATH, formName, formVersion));
            String fileContent = translationFile.exists() ? FileUtils.readFileToString(translationFile) : "";
            jsonObject = isNotEmpty(fileContent) ? new JSONObject(fileContent) : new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
