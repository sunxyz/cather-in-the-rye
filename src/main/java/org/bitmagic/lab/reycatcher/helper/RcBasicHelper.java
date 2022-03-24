package org.bitmagic.lab.reycatcher.helper;

import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.ex.BasicException;
import org.bitmagic.lab.reycatcher.ReqTokenInfo;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;
import org.bitmagic.lab.reycatcher.support.TokenParseUtils;
import org.bitmagic.lab.reycatcher.utils.Base64Utils;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

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
        ReqTokenInfo reqTokenInfo = TokenParseUtils.findReqTokenInfo(RcRequestContextHolder.getContext().getRequest().getHeader("Authorization")).orElseThrow(() -> new BasicException("not found Authorization", realm));
        ValidateUtils.checkBasic("Basic".equals(reqTokenInfo.getType()), "not basic", realm);
        ValidateUtils.checkBasic(Base64Utils.match(reqTokenInfo.getValue(), usernameAndPwd), "username or password incorrect", realm);
    }
}
