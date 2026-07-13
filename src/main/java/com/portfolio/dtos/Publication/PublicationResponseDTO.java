package com.portfolio.dtos.Publication;

import com.portfolio.dtos.AuditableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PublicationResponseDTO extends AuditableResponse {
    private Long id;
    private Long profileId;
    private String title;
    private String type;
    private String url;
    private String publisher;
    private LocalDate publishedDate;
    private String description;
    private String coAuthors;
    private Integer sortOrder;

    public PublicationResponseDTO(Long id, Long profileId, String title, String type,
                                  String url, String publisher, LocalDate publishedDate,
                                  String description, String coAuthors, Integer sortOrder,
                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                  Long createdBy, Long updatedBy,
                                  String createdByName, String updatedByName) {
        super(createdAt, updatedAt, createdBy, updatedBy, createdByName, updatedByName);
        this.id = id;
        this.profileId = profileId;
        this.title = title;
        this.type = type;
        this.url = url;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.coAuthors = coAuthors;
        this.sortOrder = sortOrder;
    }
}
