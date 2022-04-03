package org.bitmagic.lab.reycatcher.oauth2.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class OAuth2Approval {
    private String userId;
    private String clientId;
    private String scope;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime createdAt;
}
