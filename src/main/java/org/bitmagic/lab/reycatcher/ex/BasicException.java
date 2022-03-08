package org.bitmagic.lab.reycatcher.ex;

import lombok.Getter;

/**
 * @author yangrd
 */
@Getter
public class BasicException extends RyeCatcherException{

    private final String realm;

    public BasicException(String s, String realm) {
        super(s);
        this.realm = realm;
    }
}
