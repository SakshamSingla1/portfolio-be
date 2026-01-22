package com.portfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project-images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectImages {
    @Id
    private String id;
    private String projectId;
    private String profileId;
    private String url;
    private String publicId;
}
