package com.portfolio.dtos.Guestbook;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GuestbookEntryRequest {

    @NotBlank
    @Size(max = 60)
    private String visitorName;

    @NotBlank
    @Size(max = 500)
    private String message;
}
