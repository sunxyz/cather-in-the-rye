package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Value(staticConstructor = "of")
public class SessionToken {

   public interface GenTypeCons {
         String JWT = "jwt";
         String SESSION_ID = "session_id";
    }

    String genType;
    String token;
}
