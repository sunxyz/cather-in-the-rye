package org.bitmagic.lab.reycatcher.support;

import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.utils.StringUtils;

import java.util.Optional;

/**
 * @author yangrd
 */
public class TokenParseUtils {

    public static Optional<ReqTokenInfo> parseReqTokenInfo(String token) {
        String type = null;
        String value = token;
        if(StringUtils.isNotBlank(token)){
            token = token.replaceAll("%20"," ");
            String splitKey = " ";
            if (token.contains(splitKey)) {
                String[] strArray = token.split(splitKey);
                type = strArray[0];
                value = strArray[1];
            }
        }
        return StringUtils.isNotBlank(token) ? Optional.of(ReqTokenInfo.of(type, value)) : Optional.empty();
    }
}
