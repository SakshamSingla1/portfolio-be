package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "profile_theme_mapping")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileThemeMapping extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "profile_id")
    private Long profileId;

    @Column(name = "theme_id")
    private Long themeId;
}
