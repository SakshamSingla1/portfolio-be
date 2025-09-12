package com.portfolio.entities;

import com.portfolio.enums.SkillCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="logo")
public class Logo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String url;
    @Enumerated(EnumType.STRING)  // ✅ saves enum as text (FRONTEND, BACKEND, etc.)
    private SkillCategoryEnum category;
}
