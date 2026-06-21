package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.converters.LogoConverter;
import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(
    name = "skills",
    uniqueConstraints = @UniqueConstraint(name = "uk_skill_profile_logo", columnNames = {"profile_id", "logo_id"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = LogoConverter.class)
    @Column(columnDefinition = "TEXT")
    private Logo logo;

    // Denormalized for queries — synced from logo on every save
    @Column(name = "logo_id")
    private Long logoId;

    @Column(name = "logo_name")
    private String logoName;

    @Enumerated(EnumType.STRING)
    private SkillLevelEnum level;

    @Enumerated(EnumType.STRING)
    private SkillCategoryEnum category;

    @Column(name = "profile_id")
    private Long profileId;

    @PrePersist
    @PreUpdate
    private void syncLogoFields() {
        if (logo != null) {
            this.logoId = logo.getId();
            this.logoName = logo.getName();
        }
    }
}
