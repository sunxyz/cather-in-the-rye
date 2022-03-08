package org.bitmagic.lab.reycatcher.utils;

import org.bitmagic.lab.reycatcher.ex.AuthorityException;
import org.bitmagic.lab.reycatcher.ex.BasicException;

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

    public static void checkAuthority(boolean pass , String error){
        if (!pass){
            throw new AuthorityException(error);
        }
    }

    public static void checkBasic(boolean pass , String error, String realm ){
        if (!pass){
            throw new BasicException(error,realm);
        }
    }
}
