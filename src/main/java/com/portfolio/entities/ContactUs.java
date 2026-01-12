package com.portfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contact-us")
public class ContactUs {

    @Id
    private String id;
    private String name;
    private String email;
    private String message;
    private String phone;
    private String profileId;
    private LocalDateTime createdAt;
}
