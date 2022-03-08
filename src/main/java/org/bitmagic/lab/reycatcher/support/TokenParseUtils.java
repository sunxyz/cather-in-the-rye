package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.utils.StringUtils;

import java.util.Optional;

/**
 * @author yangrd
 */
public class TokenParseUtils {


    public static Optional<SessionToken> getSessionToken(String token) {
        String type = ConfigHolder.getGenTokenType();
        String value = token;
        String splitKey = " ";
        if (StringUtils.isNotEmpty(token)&&token.contains(splitKey)) {
            String[] strArray = token.split(splitKey);
            type = strArray[0];
            value = strArray[1];
        }
        return StringUtils.isEmpty(token) ? Optional.of(SessionToken.of(type, value)) : Optional.empty();
    }
}
