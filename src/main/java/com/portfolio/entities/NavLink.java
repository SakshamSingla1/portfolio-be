package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)

@Entity
@Table(name = "nav_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavLink extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nav_index")
    private String index;

    private String name;
    private String path;
    private String icon;

    @Column(name = "nav_group")
    private String navGroup;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;
}
