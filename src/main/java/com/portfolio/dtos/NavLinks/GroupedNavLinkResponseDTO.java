package com.portfolio.dtos.NavLinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupedNavLinkResponseDTO {
    private String groupName;
    private List<NavLinkResponseDTO> links;
}
