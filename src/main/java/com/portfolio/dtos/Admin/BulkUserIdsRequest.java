package com.portfolio.dtos.Admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkUserIdsRequest {
    @NotEmpty(message = "At least one user id is required")
    private List<Long> ids;
}
