package org.bitmagic.lab.reycatcher.oauth2.helper;

import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.ex.BearerException;
import org.bitmagic.lab.reycatcher.oauth2.OAuth2ResourceServer;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;

/**
 * @author yangrd
 */
public class RcOauth2CheckHelper {

    private final static OAuth2ResourceServer oauth2ResourceServer = InstanceHolder.getInstance(OAuth2ResourceServer.class);

    public static void checkScope(String scope) {
        ReqTokenInfo reqTokenInfo = TokenParseUtils.parseReqTokenInfo(RcRequestContextHolder.getContext().getRequest().getHeader("Authorization")).orElseThrow(() -> new BearerException("not found Authorization header"));
        oauth2ResourceServer.checkScope(reqTokenInfo.getValue(), scope);
    }
}
