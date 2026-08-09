package com.portfolio.dtos.Common;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkIdsRequest {
    @NotEmpty(message = "At least one id is required")
    private List<Long> ids;
}
