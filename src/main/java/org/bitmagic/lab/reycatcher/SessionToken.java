package org.bitmagic.lab.reycatcher;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class SessionToken {

    interface GenTypeCons {
         String JWT = "jwt";
         String SESSION_ID = "session_id";
    }

    String genType;
    String token;
}
