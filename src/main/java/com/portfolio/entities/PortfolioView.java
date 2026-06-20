package com.portfolio.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "portfolio_views")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "view_profile_time",  def = "{ 'profileId': 1, 'timestamp': -1 }"),
        @CompoundIndex(name = "view_profile_session", def = "{ 'profileId': 1, 'sessionId': 1 }")
})
public class PortfolioView {

    @Id
    private String id;

    private String profileId;

    private String sessionId;

    private String device;   // DESKTOP | MOBILE | TABLET

    private String referrer;

    private LocalDateTime timestamp;
}
