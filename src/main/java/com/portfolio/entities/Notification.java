package com.portfolio.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.portfolio.audit.Auditable;
import com.portfolio.enums.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String link;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @JsonProperty
    public boolean isRead() {
        return isRead;
    }
}
