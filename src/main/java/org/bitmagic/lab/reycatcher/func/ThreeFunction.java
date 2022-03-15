package org.bitmagic.lab.reycatcher.func;

@FunctionalInterface
public interface ThreeFunction <T, U, V, R>{

    R apply(T var1, U var2, V var3);

}
