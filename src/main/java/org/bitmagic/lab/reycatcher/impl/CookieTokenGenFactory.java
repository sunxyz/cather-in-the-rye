package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;

import java.util.Random;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class CookieTokenGenFactory extends AbstractTokenGenFactory {
    @Override
    public SessionToken genToken(Object id, String deviceType, Object clientExtMeta) {
        return SessionToken.of(SessionToken.TokenTypeCons.COOKIE, new Random().toString().replace("-","").toLowerCase());
    }
}
