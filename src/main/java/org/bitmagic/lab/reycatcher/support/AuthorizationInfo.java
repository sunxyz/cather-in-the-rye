package org.bitmagic.lab.reycatcher.support;

import lombok.Value;

/**
 * @author yangrd
 */
@Value(staticConstructor = "of")
public class AuthorizationInfo {

     String type;

     String value;
}
