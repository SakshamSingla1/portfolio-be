package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.LanguageProficiencyEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_languages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfileLanguage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "language_name", nullable = false, length = 100)
    private String languageName;

    @Enumerated(EnumType.STRING)
    private LanguageProficiencyEnum proficiency;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
