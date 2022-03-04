package org.bitmagic.lab.reycatcher;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface TokenGenService {

    SessionToken genToken(Object id, String deviceType, String sessionTokenType, Object clientExtMeta);
}
