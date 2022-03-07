package org.bitmagic.lab.reycatcher.config.spring;

import lombok.Data;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@EnableConfigurationProperties
@ConfigurationProperties("rye-catcher")
@Data
public class RyeCatcherProperties {

    private Map<String,ConfigInfo> genTokenTypeInfo;

    public Set<String> getTokenNames(){
        return genTokenTypeInfo.keySet();
    }

    @Data
    public static class ConfigInfo{
        private String tokenName = "JSESSIONID";

        private int sessionTimeOutMillisecond = 30*60*100;

        private Boolean needSave;

        private Boolean needOutClient;

        public boolean isNeedSave() {
            if(Objects.isNull(needSave)){
                return SessionToken.TokenTypeCons.COOKIE.equals(genTokenType);
            }
            return needSave;
        }

        public boolean isNeedOutClient() {
            if(Objects.isNull(needOutClient)){
                return SessionToken.TokenTypeCons.COOKIE.equals(genTokenType);
            }
            return needOutClient;
        }
    }

}
