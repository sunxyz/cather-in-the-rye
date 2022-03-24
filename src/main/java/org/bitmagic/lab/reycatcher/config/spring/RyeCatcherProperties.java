package org.bitmagic.lab.reycatcher.config.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
    private List<CertificationSystemDefine> certificationSystems;

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    public static class CertificationSystemDefine {

        // not set val
        private String id;

        private List<String> predicates;

        private String jwtHmacSecret;

        private String genTokenType = SessionToken.GenTypeCons.SESSION_ID;

        // not repeat
        private String outClientTokenName = "JSESSIONID";

        private int sessionTimeOutMillisecond = 30*60*100;

        private boolean sessionNeedSave;

        private boolean sessionNeedOutClient;

        private boolean loginMutex;

    }


}
