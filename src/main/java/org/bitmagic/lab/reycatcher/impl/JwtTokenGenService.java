package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.SessionToken;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class JwtTokenGenService extends AbstractTokenGenService {
    @Override
    public SessionToken genToken(Object id, String deviceType,  Object clientExtMeta) {
      return SessionToken.of(SessionToken.TokenTypeCons.JWT_TOKEN, "todo.todo.todo");
    }
}
