package org.bitmagic.lab.reycatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@FunctionalInterface
public interface AuthMatchInfoProvider {
    Map<String, Collection<String>> loadAuthMatchInfo(String certificationSystemId, Object id, String deviceType);

    default Collection<String> loadAuthMatchInfo(String certificationSystemId, String type, Object id, String deviceType) {
        return loadAuthMatchInfo(certificationSystemId, id, deviceType).getOrDefault(type, Collections.emptyList());
    }
}
