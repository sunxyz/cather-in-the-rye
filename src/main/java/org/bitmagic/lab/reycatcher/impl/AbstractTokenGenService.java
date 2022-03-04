package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.TokenGenService;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public abstract class AbstractTokenGenService implements TokenGenService {
    @Override
    public SessionToken genToken(Object id, String deviceType, String sessionTokenType, Object clientExtMeta) {
        return genToken(id, deviceType, clientExtMeta);
    }

    public abstract SessionToken genToken(Object id, String deviceType, Object clientExtMeta);
}
