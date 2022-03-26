package org.bitmagic.lab.reycatcher;

/**
 * @author bloom
 */
public interface RyeCatcherActionListener {

    /**
     * 登录
     *
     * @param certificationSystemId
     * @param id
     * @param deviceType
     */
    void doLogin(String certificationSystemId, Object id, String deviceType);

    /**
     * 退出
     *
     * @param certificationSystemId
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doLogout(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 被踢出
     *
     * @param certificationSystemId
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doKicked(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 被顶替
     *
     * @param certificationSystemId
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doBeReplaced(String certificationSystemId, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 切换身份 如果  toId toDeviceType 不存的话会创建一个 存在则直接使用 ，如果是登录互斥 用户登录后对应 session 会改成用户的
     *
     * @param certificationSystemId
     * @param fromId
     * @param fromDeviceType
     * @param toId
     * @param toDeviceType
     */
    void doSwitch(String certificationSystemId, Object fromId, String fromDeviceType, Object toId, String toDeviceType);

    /**
     * 停止切换
     *
     * @param certificationSystemId
     * @param id
     * @param deviceType
     */
    void doStopSwitch(String certificationSystemId, Object id, String deviceType);
}

