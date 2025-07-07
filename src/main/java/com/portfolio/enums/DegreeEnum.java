package com.portfolio.enums;

public enum DegreeEnum {
    HIGH_SCHOOL("10th"),
    SENIOR_SECONDARY("12th"),
    BACHELORS("Bachelors"),
    MASTERS("Masters"),
    DIPLOMA("Diploma"),
    PHD("PhD"),
    OTHER("Other");

    private final String displayName;

    DegreeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
