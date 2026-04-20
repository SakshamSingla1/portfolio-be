package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.SkillCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "logos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logo extends Auditable {

    @Id
    private String id;
    private String name;
    private String url;
}

