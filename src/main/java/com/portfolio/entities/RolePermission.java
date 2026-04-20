package com.portfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "role_permissions")
public class RolePermission {
    @Id
    private String id;
    private String roleId;
    private String navLinkId;
    private String permissionId;
}
