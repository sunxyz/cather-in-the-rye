package org.bitmagic.lab.reycatcher.oauth2;

import org.bitmagic.lab.reycatcher.oauth2.model.UserInfo;

import java.util.Optional;

/**
 * @author yangrd
 */
public interface OAuth2UserInfoProvider {

    Optional<UserInfo> loadUserInfo(String userId);
}
