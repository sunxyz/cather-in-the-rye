package org.bitmagic.lab.reycatcher.config.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/06
 */
@EnableConfigurationProperties
@ConfigurationProperties("rye-catcher")
@Data
public class RyeCatcherProperties {

    /**
     * key: Path prefix value: info
     */
    private Map<String,CertificationSystemInfo> multiCertificationSystemInfo;

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    public static class CertificationSystemInfo{

        private String genTokenType = SessionToken.TokenTypeCons.COOKIE;

        // not repeat
        private String outClientTokenName = "JSESSIONID";

        private int sessionTimeOutMillisecond = 30*60*100;

        private boolean sessionNeedSave;

        private boolean sessionNeedOutClient;

        private boolean loginMutex;

        // not set val
        private String ryeCatcherPath;
    }


}
