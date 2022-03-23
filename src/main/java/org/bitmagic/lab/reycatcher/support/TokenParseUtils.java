package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.utils.StringUtils;

import java.util.Optional;

/**
 * @author yangrd
 */
public class TokenParseUtils {

    public static Optional<ReqTokenInfo> findReqTokenInfo(String token) {
        String type = null;
        String value = token;
        String splitKey = " ";
        if (StringUtils.isNotEmpty(token)&&token.contains(splitKey)) {
            String[] strArray = token.split(splitKey);
            type = strArray[0];
            value = strArray[1];
        }
        return StringUtils.isNotEmpty(token) ? Optional.of(ReqTokenInfo.of(type, value)) : Optional.empty();
    }
}
