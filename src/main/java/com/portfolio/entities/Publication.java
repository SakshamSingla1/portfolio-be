package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "publications")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    private String url;

    private String publisher;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "co_authors")
    private String coAuthors;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
