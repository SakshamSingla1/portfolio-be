package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guestbook_entries")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookEntry extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "visitor_name", nullable = false)
    private String visitorName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    // ACTIVE (publicly visible, the default on submit) or INACTIVE (owner hid it) -- reuses the
    // same ACTIVE/INACTIVE convention SocialLinks and other visitor-facing lists already use,
    // rather than introducing a parallel visible/hidden boolean.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;
}
