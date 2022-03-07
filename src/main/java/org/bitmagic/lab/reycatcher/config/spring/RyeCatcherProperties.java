package org.bitmagic.lab.reycatcher.config.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EnableConfigurationProperties
@ConfigurationProperties("rye-catcher")
@Data
public class RyeCatcherProperties {

    private Map<String,ConfigInfo> tokenType2Config;

    private Map<String,String>  pathPrefix2TokenType;

    {
        tokenType2Config = new HashMap<>();
        tokenType2Config.put(SessionToken.TokenTypeCons.COOKIE, ConfigInfo.of("JSESSIONID", 30*60*100, true, true));
        pathPrefix2TokenType = new HashMap<>();
        pathPrefix2TokenType.put("/",SessionToken.TokenTypeCons.COOKIE);
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    public static class ConfigInfo{

        private String tokenName = "JSESSIONID";

        private int sessionTimeOutMillisecond = 30*60*100;

        private boolean needSave;

        private boolean needOutClient;
}
    }

}
