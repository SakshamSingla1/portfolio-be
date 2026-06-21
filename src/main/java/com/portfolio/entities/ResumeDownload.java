package com.portfolio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_downloads")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "resume_id")
    private Long resumeId;

    private LocalDateTime downloadedAt;
}
