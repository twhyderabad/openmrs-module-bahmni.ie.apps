package org.openmrs.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.openmrs.module.bahmni.ie.apps.model.FormTranslation;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class BahmniFormTranslationServiceImpl extends BaseOpenmrsService implements BahmniFormTranslationService {

    private static String FORM_TRANSLATIONS_PATH = "/var/www/bahmni_config/openmrs/apps/forms/translations";
    private final String CONCEPT_TRANS_KEY_PATTERN = "_[0-9]+$";
    private final String DESC_TRANS_KEY_PATTERN = "_[0-9]+_DESC$";

    @Override
    public List<FormTranslation> getFormTranslations(String formName, String formVersion, String locale) {
        return mapTranslations(locale, formName, formVersion);
    }

    @Override
    public FormTranslation saveFormTranslation(FormTranslation formTranslation) {
        if (!validate(formTranslation)) {
            throw new APIException("Invalid Parameters");
        }
        String formName = formTranslation.getFormName();
        String version = formTranslation.getVersion();
        File translationFile = new File(getFileName(formName, version));
        translationFile.getParentFile().mkdirs();
        saveTranslationsToFile(formTranslation, translationFile);

        return formTranslation;
    }

    @Override
    public FormFieldTranslations setNewTranslationsForForm(String locale, String formName, String version) {
        String defaultLocale = Context.getAdministrationService().getGlobalProperty("default_locale");
        FormTranslation defaultTranslation = mapTranslations(defaultLocale, formName, version).get(0);
        HashMap<String, ArrayList<String>> conceptWithAllNames = getTranslationsForConcepts(Locale.forLanguageTag(locale), defaultTranslation.getConcepts(), Locale.forLanguageTag(defaultLocale));

        FormFieldTranslations formFieldTranslations = new FormFieldTranslations();

        formFieldTranslations.setLabelsWithAllName(defaultTranslation.getLabels().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey)));

        formFieldTranslations.setConceptsWithAllName(conceptWithAllNames);
        formFieldTranslations.setLocale(locale);

        return formFieldTranslations;
    }

    private HashMap<String, ArrayList<String>> getTranslationsForConcepts(Locale locale, Map<String, String> conceptTranslations, Locale defaultLocale) {
        HashMap<String, ArrayList<String>> conceptWithAllNames = new HashMap<>();
        ConceptService conceptService = Context.getConceptService();
        for (String key : conceptTranslations.keySet()) {
            HashSet<String> translations = getLocaleTranslations(locale, conceptTranslations, defaultLocale, conceptService, key);
            conceptWithAllNames.put(key, new ArrayList<>(translations));
        }
        return conceptWithAllNames;
    }

    private HashSet<String> getLocaleTranslations(Locale locale, Map<String, String> conceptTranslations, Locale defaultLocale, ConceptService conceptService, String key) {
        HashSet<String> translations = new HashSet<>();
        if (locale.equals(defaultLocale))
            translations.add(conceptTranslations.get(key));
        if (key.matches(String.format(".*%s", CONCEPT_TRANS_KEY_PATTERN)))
            translations.addAll(getConceptNames(locale, conceptService, key));
        else if (key.matches(String.format(".*%s", DESC_TRANS_KEY_PATTERN)))
            translations.addAll(getDescriptions(locale, conceptService, key));
        return translations;
    }

    private String getFileName(String formName, String version) {
        return String.format("%s/%s_%s.json", FORM_TRANSLATIONS_PATH, formName, version);
    }

    private void saveTranslationsToFile(FormTranslation formTranslation, File translationFile) {
        try {
            JSONObject translations = new JSONObject();
            translations.put("labels", new JSONObject(formTranslation.getLabels()));
            translations.put("concepts", new JSONObject(formTranslation.getConcepts()));

            JSONObject translationsJson = getTranslations(translationFile);
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

    private List<FormTranslation> mapTranslations(String locale, String formName, String formVersion) {
        JSONObject jsonObject = getTranslationJsonFromFile(formName, formVersion);
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
        try {
            File translationFile = new File(getFileName(formName, formVersion));
            if (!translationFile.exists())
                throw new APIException(String.format("Unable to find translation file for %s_v%s", formName, formVersion));
            return getTranslations(translationFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new APIException(e.getMessage(), e);
        }
    }

    private JSONObject getTranslations(File translationFile) throws IOException {
        String fileContent = translationFile.exists() ? FileUtils.readFileToString(translationFile) : "";
        return isNotEmpty(fileContent) ? new JSONObject(fileContent) : new JSONObject();
    }


    private HashSet<String> getDescriptions(Locale locale, ConceptService conceptService, String key) {
        HashSet<String> descriptions = new HashSet<>();
        Concept concept = getConceptByName(conceptService, getDescConceptName(key));
        String description = key;
        if (concept != null) {
            ConceptDescription conceptDescription = concept.getDescription(locale);
            if (conceptDescription != null) {
                description = conceptDescription.getDescription();
            }
        }
        descriptions.add(description);
        return descriptions;
    }

    private HashSet<String> getConceptNames(Locale locale, ConceptService conceptService, String key) {
        HashSet<String> conceptNames = new HashSet<>();
        String conceptName = getConceptNameFromKey(key);

        Concept concept = getConceptByName(conceptService, conceptName);
        if (concept != null) {
            Collection<ConceptName> synonyms = concept.getSynonyms(locale);
            conceptNames.add(getTranslatedValue(locale, key, concept.getName(locale)));
            conceptNames.add(getTranslatedValue(locale, key, concept.getShortNameInLocale(locale)));

            if (!synonyms.isEmpty()) {
                conceptNames.addAll(synonyms.stream().map(ConceptName::getName).collect(Collectors.toList()));
            }
        }
        return conceptNames;
    }

    private String getTranslatedValue(Locale locale, String key, ConceptName conceptName) {
        return isTranslationAvailable(locale, conceptName) ? conceptName.getName() : key;
    }

    private boolean isTranslationAvailable(Locale locale, ConceptName conceptName) {
        return conceptName != null && conceptName.getLocale().equals(locale);
    }

    private Concept getConceptByName(ConceptService conceptService, String conceptName) {
        List<Concept> conceptsByName = conceptService.getConceptsByName(conceptName);
        for (Concept concept : conceptsByName) {
            ConceptName name = concept.getName();
            if (concept.getName().getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED) && name.getName().equalsIgnoreCase(conceptName)) {
                return concept;
            }
        }
        return null;
    }

    private String getDescConceptName(String key) {
        return getString(key, DESC_TRANS_KEY_PATTERN);
    }

    private String getString(String key, String regexp) {
        return (key.replaceFirst(regexp, "")).replaceAll("_", " ");
    }

    private String getConceptNameFromKey(String key) {
        return getString(key, CONCEPT_TRANS_KEY_PATTERN);
    }

}
