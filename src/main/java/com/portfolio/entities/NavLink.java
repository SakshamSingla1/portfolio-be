package com.portfolio.entities;

import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nav-links")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_roles_status",
                def = "{ 'roles': 1, 'status': 1 }"
        ),
        @CompoundIndex(
                name = "idx_roles_path_unique",
                def = "{ 'roles': 1, 'path': 1 }",
                unique = true
        )
})
public class NavLink {
    @Id
    private String id;
    private String index;
    @TextIndexed
    private String name;
    @Indexed
    private String path;
    private String icon;
    @Indexed
    private List<String> roles;
    @Indexed
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
