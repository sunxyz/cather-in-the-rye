package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface LoginInfo {

    static LoginInfo of(Object userId, String deviceType){
        return  SimpleIdentityInfo.of(userId,deviceType);
    }

    Object getUserId();

    String getDeviceType();

    @Value(staticConstructor = "of")
    class SimpleIdentityInfo implements LoginInfo {
        Object userId;

        String deviceType;
    }

}
