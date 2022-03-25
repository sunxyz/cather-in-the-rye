package org.bitmagic.lab.reycatcher.func;

/**
 * @author yangrd
 */
@FunctionalInterface
public interface ThreePredicate <T, U, O> {

    boolean test(T var1, U var2,O var3);
}
