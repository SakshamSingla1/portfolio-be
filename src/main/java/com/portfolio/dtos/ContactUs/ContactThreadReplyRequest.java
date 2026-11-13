package com.portfolio.dtos.ContactUs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactThreadReplyRequest {

    @NotBlank
    @Size(max = 2000)
    private String message;
}
