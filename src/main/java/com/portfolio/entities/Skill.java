// Skill.java
package com.portfolio.entities;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "logo_id", nullable = false)
    private Logo logo;

    @Enumerated(EnumType.STRING)
    private SkillLevelEnum level;
}
