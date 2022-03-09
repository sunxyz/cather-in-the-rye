package org.bitmagic.lab.reycatcher.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionToken;

/**
 * @author bloom
 */
@Slf4j
public class DefaultRyeCatcherActionListener implements RyeCatcherActionListener {
    @Override
    public void doLogin(String ryeCatcherPath, Object id, String deviceType) {
        log.debug("doLogin ryeCatcherPath:{} id:{} deviceType:{}", ryeCatcherPath, id, deviceType);
    }

    @Override
    public void doLogout(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doLogout ryeCatcherPath:{} id:{} deviceType:{}", ryeCatcherPath, id, deviceType);
    }

    @Override
    public void doKicked(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doKicked ryeCatcherPath:{} id:{} deviceType:{}", ryeCatcherPath, id, deviceType);
    }

    @Override
    public void doBeReplaced(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doBeReplaced ryeCatcherPath:{} id:{} deviceType:{}", ryeCatcherPath, id, deviceType);
    }
}
