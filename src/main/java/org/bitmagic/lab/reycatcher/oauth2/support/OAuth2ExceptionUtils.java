package org.bitmagic.lab.reycatcher.oauth2.support;

/**
 * @author yangrd
 */
public class OAuth2ExceptionUtils {

    public static void tryOauth2Exception(boolean isTry, String error) {
        tryException(isTry, new OAuth2Exception(error));
    }

    private static void tryException(boolean isTry, OAuth2Exception e) {
        if (isTry) {
            throw e;
        }
    }
}
