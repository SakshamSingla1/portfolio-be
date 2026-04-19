package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.RoleStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role extends Auditable {
    @Id
    private String id;
    private String name;
    private String description;
    private RoleStatusEnum status;
}
