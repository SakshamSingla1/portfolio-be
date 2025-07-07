package com.portfolio.exceptions;

import com.portfolio.enums.ExceptionCodeEnum;
import lombok.*;
import org.springframework.web.bind.annotation.ControllerAdvice;

@RequiredArgsConstructor
@ControllerAdvice
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=false)
@Data
public class GenericException extends Exception{
    public ExceptionCodeEnum exceptionCode;
    public String errorMessage;
    public String referenceId;

    public GenericException(ExceptionCodeEnum exceptionCode, String errorMessage) {
        this.exceptionCode = exceptionCode;
        this.errorMessage = errorMessage;
    }
}