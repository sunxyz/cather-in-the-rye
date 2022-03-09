package org.bitmagic.lab.reycatcher;

import java.util.Optional;

/**
 * @author yangrd
 */
public interface IdentitySwitch {

    void switchId(LoginInfo from, LoginInfo to);

    Optional<LoginInfo> findSwitchIdTo(LoginInfo from);
}
