package org.bitmagic.lab.reycatcher.impl;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.AuthMatchInfoProvider;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@RequiredArgsConstructor
public class JwtSessionTokenGenFactory extends AbstractSessionTokenGenFactory {

    @Override
    public SessionToken genToken(Object id, String deviceType, Object clientExtMeta) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put("deviceType", deviceType);
        if (clientExtMeta instanceof Map) {
            ((Map<String, Object>) clientExtMeta).forEach((k, v) -> {
                claims.put("ext-" + k, v);
            });
        } else {
            claims.put("ext", clientExtMeta);
        }
        if (DynamicRcConfigHolder.isEnableJwtAuthMatchInfo()) {
            InstanceHolder.getInstance(AuthMatchInfoProvider.class).loadAuthMatchInfo(DynamicRcConfigHolder.getCertificationSystemId(), id, deviceType).forEach((k, v) -> {
                claims.put("auth-" + k, v);
            });
        }
        String token = JwtUtils.createToken(DynamicRcConfigHolder.getAlgorithm(), id.toString(), System.currentTimeMillis() + DynamicRcConfigHolder.getSessionTimeoutMillisecond(), claims);
        return SessionToken.of(SessionToken.GenTypeCons.JWT, token);
    }
}
