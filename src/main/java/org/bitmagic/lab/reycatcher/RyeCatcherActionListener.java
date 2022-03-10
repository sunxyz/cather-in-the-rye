package org.bitmagic.lab.reycatcher;

/**
 * @author bloom
 */
public interface RyeCatcherActionListener {

    /**
     * 登录
     * @param ryeCatcherPath
     * @param id
     * @param deviceType
     */
    void doLogin(String ryeCatcherPath, Object id, String deviceType);

    /**
     * 退出
     * @param ryeCatcherPath
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doLogout(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 被踢出
     * @param ryeCatcherPath
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doKicked(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 被顶替
     * @param ryeCatcherPath
     * @param id
     * @param deviceType
     * @param sessionToken
     */
    void doBeReplaced(String ryeCatcherPath, Object id, String deviceType, SessionToken sessionToken);

    /**
     * 切换身份 如果  toId toDeviceType 不存的话会创建一个 存在则直接使用 ，如果是登录互斥 用户登录后对应 session 会改成用户的
     * @param ryeCatcherPath
     * @param fromId
     * @param fromDeviceType
     * @param toId
     * @param toDeviceType
     */
    void doSwitch(String ryeCatcherPath, Object fromId, String fromDeviceType, Object toId, String toDeviceType);

    /**
     * 停止切换
     * @param ryeCatcherPath
     * @param id
     * @param deviceType
     */
    void doStopSwitch(String ryeCatcherPath, Object id, String deviceType);
}

