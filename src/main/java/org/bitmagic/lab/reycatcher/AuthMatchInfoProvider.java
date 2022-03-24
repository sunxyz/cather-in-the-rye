package org.bitmagic.lab.reycatcher;

import java.util.Collection;
import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface AuthMatchInfoProvider {
    Map<String , Collection<String>> loadAuthMatchInfo(String certificationSystemId, Object id, String deviceType);
}
