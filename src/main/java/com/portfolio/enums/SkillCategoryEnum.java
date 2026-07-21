package com.portfolio.enums;

public enum SkillCategoryEnum {
    FRONTEND("Frontend"),             // HTML, CSS, React, Angular
    BACKEND("Backend"),               // Java, Spring Boot, Node.js
    PROGRAMMING("Programming"),       // Core languages like Java, Python, C++
    TOOL("Tools"),                    // Git, Docker, Jenkins, Postman
    DATABASE("Database"),             // MySQL, MongoDB, PostgreSQL
    DEVOPS("DevOps"),                 // CI/CD, Kubernetes, AWS, GCP
    TESTING("Testing"),               // JUnit, Selenium, Cypress
    MOBILE("Mobile"),                 // React Native, Flutter, Android, iOS
    CLOUD("Cloud"),                   // AWS, Azure, Google Cloud
    SECURITY("Security"),             // Authentication, OAuth2, JWT, Encryption
    DATA_SCIENCE("Data Science"),     // ML, AI, Pandas, NumPy
    UI_UX("UI/UX"),                   // Figma, Adobe XD, UI principles
    SOFT_SKILLS("Soft Skills"),       // Communication, Teamwork, Leadership
    OTHER("Other");                  // Anything uncategorized

    private final String displayName;

    SkillCategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
