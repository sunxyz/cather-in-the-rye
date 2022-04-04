package org.bitmagic.lab.reycatcher.oauth2.support;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;

@EqualsAndHashCode(callSuper = true)
@Value
public class OAuth2Exception extends RyeCatcherException {

    private static final long serialVersionUID = 1L;

    public OAuth2Exception(String message) {
        super(message);
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
