package org.bitmagic.lab.reycatcher.func;

/**
 * @author yangrd
 */
@FunctionalInterface
public interface ThreePredicate <T, U, O> {

    /**
     * ThreePredicate
     * @param var1
     * @param var2
     * @param var3
     * @return
     */
    boolean test(T var1, U var2,O var3);
}
