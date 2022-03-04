package org.bitmagic.lab.reycatcher.utils;

import org.bitmagic.lab.reycatcher.ex.AuthorityException;

import java.util.Objects;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public final class ValidateUtils {

    public static void notNull(Object o, String error){
        if (Objects.isNull(o)){
            throw new IllegalArgumentException(error);
        }
    }

    public static void checkAuthority(boolean flag , String error){
        if (!flag){
            throw new AuthorityException(error);
        }
    }
}
