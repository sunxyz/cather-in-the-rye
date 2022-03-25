package org.bitmagic.lab.reycatcher;


import lombok.Value;

/**
 * @author yangrd
 */
@Value(staticConstructor = "of")
public class TokenInfo {

    String accessToken;
    Integer expiresIn;
    String scope;
    String type;
    String tokenName;
}
