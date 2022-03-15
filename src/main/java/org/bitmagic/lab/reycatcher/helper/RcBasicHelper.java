package org.bitmagic.lab.reycatcher.helper;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.support.AuthorizationInfo;
import org.bitmagic.lab.reycatcher.support.RyeCatcherContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

/**
 * @author yangrd
 * @date 2022/03/08
 */
@Slf4j
public class RcBasicHelper {

    public static void check(String usernameAndPwd) {
        check(usernameAndPwd, "realm");
    }

    public static void check(String usernameAndPwd, String realm) {
        AuthorizationInfo authorizationInfo = TokenParseUtils.findAuthorizationInfo(RyeCatcherContextHolder.getContext().getRequest().getHeader("Authorization")).orElseThrow(() -> new BasicException("not found Authorization", realm));
        ValidateUtils.checkBasic("Basic".equals(authorizationInfo.getType()), "not basic", realm);
        ValidateUtils.checkBasic(Base64Utils.match(authorizationInfo.getValue(), usernameAndPwd), "username or password incorrect", realm);
    }
}
