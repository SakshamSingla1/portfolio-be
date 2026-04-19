package com.portfolio.dtos.Admin;

import com.portfolio.enums.StatusEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class StatusUpdateRequest {
    private StatusEnum status;
}
