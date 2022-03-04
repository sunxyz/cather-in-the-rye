package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface LoginInfo {

    static LoginInfo of(Object userId, String deviceType){
        return SimpleLoginInfo.of(userId,deviceType);
    }

    Object getUserId();

    String getDeviceType();

    @Value(staticConstructor = "of")
    class SimpleLoginInfo implements LoginInfo{
        Object userId;

        String deviceType;
    }

}
