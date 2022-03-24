package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.utils.IdGenerator;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class SessionIdSessionTokenGenFactory extends AbstractSessionTokenGenFactory {
    @Override
    public SessionToken genToken(Object id, String deviceType, Object clientExtMeta) {
        return SessionToken.of(SessionToken.GenTypeCons.SESSION_ID, IdGenerator.genUuid());
    }
}
