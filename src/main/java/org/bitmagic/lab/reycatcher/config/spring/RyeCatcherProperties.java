package org.bitmagic.lab.reycatcher.config.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@ConfigurationProperties("rye-catcher")
@Data
public class RyeCatcherProperties {

    /**
     * key: Path prefix value: info
     */
    private Set<CertificationSystemDefine> certificationSystems;

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode(of = "id")
    @NoArgsConstructor
    public static class CertificationSystemDefine {

        // not set val
        private String id;

        private List<String> predicates;

        private String genTokenType = SessionToken.GenTypeCons.SESSION_ID;

        private String jwtHmacSecret;

        private boolean enableJwtAuthMathInfo;

        // not repeat
        private String outClientTokenName = "JSESSIONID";

        private int sessionTimeOutMillisecond = 30*60*1000;

        private boolean sessionNeedSave;

        private boolean tokenNeedOutClient;

        private boolean sameDriveMutex;

    }


}
