package com.mxolisi.resmanagement.data;

public enum ReportStatus {
    OPENED("Report is opened"),
    NOT_OPENED("Report is not opened"),
    CLOSED("Report is closed");


    private final String description;
    ReportStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
