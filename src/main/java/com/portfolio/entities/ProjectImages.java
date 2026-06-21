package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "profile_id")
    private Long profileId;

    private String url;
    private String publicId;
}
