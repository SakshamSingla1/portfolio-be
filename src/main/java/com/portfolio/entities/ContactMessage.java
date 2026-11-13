package com.portfolio.entities;

import com.portfolio.audit.Auditable;
import com.portfolio.enums.SenderTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// One message within a ContactUs conversation thread. The conversation's very first message is
// NOT stored here -- it's ContactUs's own name/message fields, synthesized into the thread at
// read time (see ContactUsServiceImpl#buildThread) -- so this table only ever holds the OWNER's
// and VISITOR's replies that come after it.
@Entity
@Table(name = "contact_messages")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contact_us_id", nullable = false)
    private Long contactUsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderTypeEnum senderType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
}
