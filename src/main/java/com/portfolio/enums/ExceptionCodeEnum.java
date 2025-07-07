package com.portfolio.enums;

import java.util.HashMap;
import java.util.Map;

public enum ExceptionCodeEnum {

    // ==== COMMON / SYSTEM ERRORS ====
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    UNEXPECTED_ERROR("UNEXPECTED_ERROR"),
    DATABASE_ERROR("DATABASE_ERROR"),
    NULL_POINTER("NULL_POINTER"),
    MAPPING_ERROR("MAPPING_ERROR"),
    FORMAT_ERROR("FORMAT_ERROR"),

    // === PROJECT ERRORS ====
    DUPLICATE_PROJECT("DUPLICATE_PROJECT"),
    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND"),

    DUPLICATE_EMAIL("DUPLICATE_EMAIL"),
    ADMIN_ALREADY_EXISTS("ADMIN_ALREADY_EXISTS"),


    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND"),
    DUPLICATE_PROFILE("DUPLICATE_PROFILE"),

    // === CONTACT US ERRORS ====
    CONTACT_US_NOT_FOUND("CONTACT_US_NOT_FOUND"),

    // === CONTACT US ERRORS ====
    EDUCATION_NOT_FOUND("EDUCATION_NOT_FOUND"),
    DUPLICATE_DEGREE("DUPLICATE_DEGREE"),

    SKILL_NOT_FOUND("SKILL_NOT_FOUND"),
    DUPLICATE_SKILL("DUPLICATE_SKILL"),

    EXPERIENCE_NOT_FOUND("EXPERIENCE_NOT_FOUND"),
    DUPLICATE_EXPERIENCE("DUPLICATE_EXPERIENCE"),
    // ==== AUTH & SECURITY ====
    UNAUTHORIZED("UNAUTHORIZED"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    TOKEN_EXPIRED("TOKEN_EXPIRED"),
    TOKEN_INVALID("TOKEN_INVALID"),
    FORBIDDEN("FORBIDDEN"),

    // ==== VALIDATION / INPUT ====
    BAD_REQUEST("BAD_REQUEST"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    PASSWORD_TOO_WEAK("PASSWORD_TOO_WEAK"),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH"),
    PASSWORD_IS_EMPTY("PASSWORD_IS_EMPTY"),
    PASSWORD_RESET_FAILED("PASSWORD_RESET_FAILED");

    private final String value;

    private static final Map<String, ExceptionCodeEnum> valueToEnumMap = new HashMap<>();

    static {
        for (ExceptionCodeEnum code : ExceptionCodeEnum.values()) {
            valueToEnumMap.put(code.getValue().toUpperCase(), code);
        }
    }

    ExceptionCodeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExceptionCodeEnum fromValue(String value) {
        ExceptionCodeEnum code = valueToEnumMap.get(value.toUpperCase());
        if (code != null) {
            return code;
        }
        throw new IllegalArgumentException("Invalid exception code: " + value);
    }
}
