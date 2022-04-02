package org.bitmagic.lab.reycatcher.config;

import lombok.*;
import org.bitmagic.lab.reycatcher.SessionToken;

import java.util.List;

/**
 * @author yangrd
 */
@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Builder
public class CertificationSystemDefine {

    private String id;

    private List<String> predicates;

    private String genTokenType = SessionToken.GenTypeCons.SESSION_ID;

    private String jwtHmacSecret;

    private boolean enableJwtAuthMatchInfoPayload;

    /**
     * not repeat
     */
    private String outClientTokenName = "JSESSIONID";

    private int sessionTimeOutMillisecond = 30*60*1000;

    private boolean sessionNeedSave;

    private boolean tokenNeedOutClient;

    private boolean sameDriveMutex;
}
