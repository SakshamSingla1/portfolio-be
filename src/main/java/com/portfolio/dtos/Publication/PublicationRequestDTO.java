package com.portfolio.dtos.Publication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PublicationRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Type is required")
    private String type;

    private String url;
    private String publisher;
    private LocalDate publishedDate;
    private String description;
    private String coAuthors;
    private Integer sortOrder;
    private Long profileId;
}
