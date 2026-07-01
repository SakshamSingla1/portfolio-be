package com.portfolio.exceptions;

import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.payload.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<ExceptionCodeEnum> UNAUTHORIZED_CODES = Set.of(
            ExceptionCodeEnum.UNAUTHORIZED,
            ExceptionCodeEnum.INVALID_CREDENTIALS,
            ExceptionCodeEnum.TOKEN_EXPIRED,
            ExceptionCodeEnum.TOKEN_INVALID
    );

    private static final Set<ExceptionCodeEnum> BAD_REQUEST_CODES = Set.of(
            ExceptionCodeEnum.BAD_REQUEST,
            ExceptionCodeEnum.INVALID_ARGUMENT,
            ExceptionCodeEnum.VALIDATION_FAILED,
            ExceptionCodeEnum.PASSWORD_TOO_WEAK,
            ExceptionCodeEnum.PASSWORD_MISMATCH,
            ExceptionCodeEnum.PASSWORD_IS_EMPTY,
            ExceptionCodeEnum.PASSWORD_RESET_FAILED,
            ExceptionCodeEnum.FORMAT_ERROR
    );

    private static final Set<ExceptionCodeEnum> CONFLICT_CODES = Set.of(
            ExceptionCodeEnum.DUPLICATE_PROJECT,
            ExceptionCodeEnum.DUPLICATE_EMAIL,
            ExceptionCodeEnum.ADMIN_ALREADY_EXISTS,
            ExceptionCodeEnum.DUPLICATE_PROFILE,
            ExceptionCodeEnum.DUPLICATE_TEMPLATE,
            ExceptionCodeEnum.DUPLICATE_LOGO,
            ExceptionCodeEnum.COLOR_THEME_ALREADY_EXISTS,
            ExceptionCodeEnum.DUPLICATE_NAV_LINK,
            ExceptionCodeEnum.DUPLICATE_SOCIAL_LINK,
            ExceptionCodeEnum.DUPLICATE_CERTIFICATION,
            ExceptionCodeEnum.DUPLICATE_TESTIMONIAL,
            ExceptionCodeEnum.DUPLICATE_ACHIEVEMENT,
            ExceptionCodeEnum.DUPLICATE_ROLE,
            ExceptionCodeEnum.DUPLICATE_PERMISSION,
            ExceptionCodeEnum.DUPLICATE_ROLE_PERMISSION,
            ExceptionCodeEnum.DUPLICATE_SKILL,
            ExceptionCodeEnum.DUPLICATE_EXPERIENCE,
            ExceptionCodeEnum.DUPLICATE_DEGREE
    );

    private static final Set<ExceptionCodeEnum> NOT_FOUND_CODES = Set.of(
            ExceptionCodeEnum.PROJECT_NOT_FOUND,
            ExceptionCodeEnum.PROFILE_NOT_FOUND,
            ExceptionCodeEnum.CONTACT_US_NOT_FOUND,
            ExceptionCodeEnum.EDUCATION_NOT_FOUND,
            ExceptionCodeEnum.SKILL_NOT_FOUND,
            ExceptionCodeEnum.EXPERIENCE_NOT_FOUND,
            ExceptionCodeEnum.TEMPLATE_NOT_FOUND,
            ExceptionCodeEnum.LOGO_NOT_FOUND,
            ExceptionCodeEnum.COLOR_THEME_NOT_FOUND,
            ExceptionCodeEnum.NAV_LINK_NOT_FOUND,
            ExceptionCodeEnum.RESUME_NOT_FOUND,
            ExceptionCodeEnum.SOCIAL_LINK_NOT_FOUND,
            ExceptionCodeEnum.CERTIFICATION_NOT_FOUND,
            ExceptionCodeEnum.TESTIMONIAL_NOT_FOUND,
            ExceptionCodeEnum.ACHIEVEMENT_NOT_FOUND,
            ExceptionCodeEnum.ROLE_NOT_FOUND,
            ExceptionCodeEnum.PERMISSION_NOT_FOUND,
            ExceptionCodeEnum.ROLE_PERMISSION_NOT_FOUND,
            ExceptionCodeEnum.LANDING_FEATURE_NOT_FOUND,
            ExceptionCodeEnum.LANDING_FAQ_NOT_FOUND,
            ExceptionCodeEnum.LANDING_STEP_NOT_FOUND,
            ExceptionCodeEnum.LANDING_AUDIENCE_CARD_NOT_FOUND,
            ExceptionCodeEnum.LANDING_TESTIMONIAL_NOT_FOUND,
            ExceptionCodeEnum.DATA_NOT_FOUND
    );

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ResponseModel<Void>> handleGenericException(GenericException ex) {
        ExceptionCodeEnum code = ex.getExceptionCode();
        String message = ex.getErrorMessage() != null ? ex.getErrorMessage() : "An error occurred";
        log.warn("GenericException [{}]: {}", code, message);

        HttpStatus status = resolveStatus(code);
        ResponseModel<Void> body = new ResponseModel<>(message, null, code != null ? code.getValue() : "ERROR");
        body.toFailure();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ResponseModel<Void>> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
        log.warn("InvalidDataAccessApiUsageException: {}", ex.getMessage());
        ResponseModel<Void> body = new ResponseModel<>("Invalid query parameter. Check your sort or filter values.", null, ExceptionCodeEnum.BAD_REQUEST.getValue());
        body.toFailure();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseModel<Void>> handleMissingHeader(MissingRequestHeaderException ex) {
        String message = "Authorization header is required";
        log.warn("MissingRequestHeaderException: {}", ex.getHeaderName());
        ResponseModel<Void> body = new ResponseModel<>(message, null, ExceptionCodeEnum.UNAUTHORIZED.getValue());
        body.toFailure();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ResponseModel<Void>> handleInvalidDataAccessResource(InvalidDataAccessResourceUsageException ex) {
        log.warn("InvalidDataAccessResourceUsageException: {}", ex.getMessage());
        ResponseModel<Void> body = new ResponseModel<>("Invalid query. Check your search or filter values.", null, ExceptionCodeEnum.BAD_REQUEST.getValue());
        body.toFailure();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseModel<Void>> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        ResponseModel<Void> body = new ResponseModel<>("An unexpected error occurred. Please try again.", null,
                ExceptionCodeEnum.INTERNAL_SERVER_ERROR.getValue());
        body.toInternalServerError();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private HttpStatus resolveStatus(ExceptionCodeEnum code) {
        if (code == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        if (UNAUTHORIZED_CODES.contains(code)) return HttpStatus.UNAUTHORIZED;
        if (code == ExceptionCodeEnum.FORBIDDEN) return HttpStatus.FORBIDDEN;
        if (NOT_FOUND_CODES.contains(code)) return HttpStatus.NOT_FOUND;
        if (BAD_REQUEST_CODES.contains(code)) return HttpStatus.BAD_REQUEST;
        if (CONFLICT_CODES.contains(code)) return HttpStatus.CONFLICT;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
