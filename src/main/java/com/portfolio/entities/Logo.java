package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "logos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Transient
    private String url;
}
