package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// visitorId is a client-generated UUID (localStorage, not real auth) -- the unique constraint is
// a server-side backstop against the same browser double-submitting, not a security boundary.
@Entity
@Table(name = "portfolio_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "visitor_id"}))
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioLike extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "visitor_id", nullable = false)
    private String visitorId;
}
