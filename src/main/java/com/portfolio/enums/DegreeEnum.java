package com.portfolio.enums;

public enum DegreeEnum {
    HIGH_SCHOOL("10th"),
    SENIOR_SECONDARY("12th"),

    DIPLOMA("Diploma"),
    ADVANCED_DIPLOMA("Advanced Diploma"),

    ASSOCIATE("Associate Degree"),

    BACHELORS("Bachelors"),
    BTECH("Bachelor of Technology"),
    BE("Bachelor of Engineering"),
    BSC("Bachelor of Science"),
    BA("Bachelor of Arts"),
    BCOM("Bachelor of Commerce"),
    BCA("Bachelor of Computer Applications"),
    BBA("Bachelor of Business Administration"),

    MASTERS("Masters"),
    MTECH("Master of Technology"),
    ME("Master of Engineering"),
    MSC("Master of Science"),
    MA("Master of Arts"),
    MCOM("Master of Commerce"),
    MBA("Master of Business Administration"),
    MCA("Master of Computer Applications"),

    PHD("PhD"),
    POST_DOCTORATE("Post Doctorate"),

    CERTIFICATION("Certification"),
    PROFESSIONAL_CERTIFICATE("Professional Certificate"),

    OTHER("Other");

    private final String displayName;

    DegreeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
