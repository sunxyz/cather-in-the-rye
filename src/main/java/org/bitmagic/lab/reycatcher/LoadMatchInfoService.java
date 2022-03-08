package org.bitmagic.lab.reycatcher;

import java.util.Collection;
import java.util.Map;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface LoadMatchInfoService {
    Map<String , Collection<String>> loadMatchInfo(String ryeCatcherPath, Object id, String deviceType);
}
