package com.portfolio.dtos.Admin;

import com.portfolio.enums.StatusEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkStatusUpdateRequest {
    @NotEmpty(message = "At least one user id is required")
    private List<Long> ids;
    @NotNull(message = "Status is required")
    private StatusEnum status;
}
