package org.bitmagic.lab.reycatcher.ex;

/**
 * @author yangrd
 */
public class RyeCatcherException extends RuntimeException{

    public RyeCatcherException() {
        super();
    }

    public RyeCatcherException(String s) {
        super(s);
    }

    public RyeCatcherException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RyeCatcherException(Throwable throwable) {
        super(throwable);
    }

    protected RyeCatcherException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
