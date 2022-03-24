package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Value(staticConstructor = "of")
public class LoginInfo {

    Object userId;

    String deviceType;
}
