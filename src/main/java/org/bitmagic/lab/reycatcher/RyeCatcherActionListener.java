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
}
