package org.bitmagic.lab.reycatcher.impl;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionToken;

/**
 * @author bloom
 */
@Slf4j
public class DefaultRyeCatcherActionListener implements RyeCatcherActionListener {
    @Override
    public void doLogin(String certificationSystemId, Object id, String deviceType) {
        log.debug("doLogin certificationSystemId:{} id:{} deviceType:{}", certificationSystemId, id, deviceType);
    }

    @Override
    public void doLogout(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doLogout certificationSystemId:{} id:{} deviceType:{}", certificationSystemId, id, deviceType);
    }

    @Override
    public void doKicked(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doKicked certificationSystemId:{} id:{} deviceType:{}", certificationSystemId, id, deviceType);
    }

    @Override
    public void doBeReplaced(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken) {
        log.debug("doBeReplaced certificationSystemId:{} id:{} deviceType:{}", certificationSystemId, id, deviceType);
    }

    @Override
    public void doSwitch(String certificationSystemId, Object fromId, String fromDeviceType, Object toId, String toDeviceType) {
        log.debug("doSwitch certificationSystemId:{} fromId:{} fromDeviceType:{} toId:{} toDeviceType:{}", certificationSystemId, fromId, fromDeviceType, toId, toDeviceType);
    }

    @Override
    public void doStopSwitch(String certificationSystemId, Object id, String deviceType) {
        log.debug("doStopSwitch certificationSystemId:{} id:{} deviceType:{}", certificationSystemId, id, deviceType);
    }
}
