package com.portfolio.entities;

import com.portfolio.enums.ContactUsStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contact-us")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_profile_created",
                def = "{ 'profileId': 1, 'createdAt': -1 }"
        ),
        @CompoundIndex(
                name = "idx_profile_status",
                def = "{ 'profileId': 1, 'status': 1 }"
        )
})
public class ContactUs {

    @Id
    private String id;

    @TextIndexed
    private String name;

    @TextIndexed
    private String email;
    private String message;

    @TextIndexed
    private String phone;

    @Indexed
    private String profileId;

    @Indexed
    private ContactUsStatusEnum status;

    @Indexed
    private LocalDateTime createdAt;
}
