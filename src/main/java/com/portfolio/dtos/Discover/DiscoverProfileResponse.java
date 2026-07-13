package com.portfolio.dtos.Discover;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DiscoverProfileResponse {
    private Long id;
    private String fullName;
    private String userName;
    private String title;
    private String location;
    private String profileImageUrl;
    private List<String> topSkills;
}
