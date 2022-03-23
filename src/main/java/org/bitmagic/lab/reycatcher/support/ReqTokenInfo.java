package org.bitmagic.lab.reycatcher.support;

import lombok.Value;

/**
 * @author yangrd
 */
@Value(staticConstructor = "of")
public class ReqTokenInfo {

     String type;

     String value;
}
