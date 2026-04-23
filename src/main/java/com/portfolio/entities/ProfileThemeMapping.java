package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "profile_theme_mapping")
public class ProfileThemeMapping extends Auditable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String profileId;

    @Indexed
    private String themeId;
}