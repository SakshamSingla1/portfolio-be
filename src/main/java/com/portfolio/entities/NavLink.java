package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nav-links")
public class NavLink extends Auditable {
    @Id
    private String id;
    private String index;
    @TextIndexed
    private String name;
    @Indexed
    private String path;
    private String icon;
    @Indexed
    private String navGroup;
    @Indexed
    private StatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
