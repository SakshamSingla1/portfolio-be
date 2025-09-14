// SkillRequest.java
package com.portfolio.dtos;

import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.SkillLevelEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {
    private Integer logoId;  // reference Logo entity
    private SkillLevelEnum level;
    private Integer profileId;
}
