package com.portfolio.dtos.Like;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeStatusResponse {

    private long totalLikes;
    private boolean likedByVisitor;
}
