package org.bitmagic.lab.reycatcher.oauth2.helper;

import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;

/**
 * @author yangrd
 */
public class RcOauth2CheckHelper {

    private final static OAuth2ResourceServer OAUTH2_RESOURCE_SERVER = InstanceHolder.getInstance(OAuth2ResourceServer.class);

    public static void checkScope(String scope) {
        OAUTH2_RESOURCE_SERVER.checkScope(RcOauth2ResourceHelper.getCurrentAccessTokenStr(), scope);
    }

    public static void checkResourceId(String resourceId) {
        OAUTH2_RESOURCE_SERVER.checkResourceId(RcOauth2ResourceHelper.getCurrentAccessTokenStr(), resourceId);
    }
}
