package com.portfolio.dtos.Admin;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private StatusEnum status;
}
