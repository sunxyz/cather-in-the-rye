package org.bitmagic.lab.reycatcher;


import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * @author yangrd
 */
@Value
@RequiredArgsConstructor(staticName = "of")
public class TokenInfo {

    String accessToken;
    Integer expiresIn;
    String scope;
    String type;
}
