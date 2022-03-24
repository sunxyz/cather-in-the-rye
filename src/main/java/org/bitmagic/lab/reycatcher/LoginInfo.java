package org.bitmagic.lab.reycatcher;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class LoginInfo {

    Object userId;

    String deviceType;
}
