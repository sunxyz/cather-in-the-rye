package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.config.ConfigHolder;
import org.bitmagic.lab.reycatcher.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class JwtSessionTokenGenFactory extends AbstractSessionTokenGenFactory {
    @Override
    public SessionToken genToken(Object id, String deviceType, Object clientExtMeta) {
        Map<String,Object> claims = new HashMap<>(3);
        claims.put("deviceType",deviceType);
        claims.put("ext", (Map) clientExtMeta);
        String token = JwtUtils.createToken(ConfigHolder.getAlgorithm(), id.toString(), System.currentTimeMillis() + ConfigHolder.getSessionTimeoutMillisecond(), claims);
        return SessionToken.of(SessionToken.TokenTypeCons.JWT, token);
    }
}
