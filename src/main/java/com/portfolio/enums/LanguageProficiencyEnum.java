package com.portfolio.enums;

public enum LanguageProficiencyEnum {
    NATIVE("Native"),
    FLUENT("Fluent"),
    INTERMEDIATE("Intermediate"),
    BASIC("Basic");

    private final String displayName;

    LanguageProficiencyEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
