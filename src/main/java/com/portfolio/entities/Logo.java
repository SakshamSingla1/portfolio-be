package com.portfolio.entities;

import com.portfolio.enums.SkillCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logo {

    @Id
    private String id;
    private String name;
    private String url;
    private SkillCategoryEnum category;
}

