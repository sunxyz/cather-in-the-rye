package org.bitmagic.lab.reycatcher.ex;

import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

/**
 * @author yangrd
 */
public class ForbiddenException extends RyeCatcherException{

    public ForbiddenException(String s) {
        super(s);
    }
}
