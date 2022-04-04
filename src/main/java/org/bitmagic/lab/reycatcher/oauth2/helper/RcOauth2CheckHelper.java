package org.bitmagic.lab.reycatcher.oauth2.helper;

import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;

/**
 * @author yangrd
 */
public class RcOauth2CheckHelper {

    private final static OAuth2ResourceServer oauth2ResourceServer = InstanceHolder.getInstance(OAuth2ResourceServer.class);

    public static void checkScope(String scope) {
        oauth2ResourceServer.checkScope(RcOAuth2Helper.getCurrentAccessTokenStr(), scope);
    }
}
