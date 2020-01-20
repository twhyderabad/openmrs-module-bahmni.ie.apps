package org.bahmni.module.bahmni.ie.apps.model;

import java.util.List;

public class ExportResponse {
    private List<BahmniFormData> bahmniFormDataList;
    private List<String> errorFormList;

    public ExportResponse(List<BahmniFormData> bahmniFormDataList, List<String> errorFormList) {
        this.bahmniFormDataList = bahmniFormDataList;
        this.errorFormList = errorFormList;
    }

    public List<BahmniFormData> getBahmniFormDataList() {
        return bahmniFormDataList;
    }

    public void setBahmniFormDataList(List<BahmniFormData> bahmniFormDataList) {
        this.bahmniFormDataList = bahmniFormDataList;
    }

    public List<String> getErrorFormList() {
        return errorFormList;
    }

    public void setErrorFormList(List<String> errorFormList) {
        this.errorFormList = errorFormList;
    }
}
