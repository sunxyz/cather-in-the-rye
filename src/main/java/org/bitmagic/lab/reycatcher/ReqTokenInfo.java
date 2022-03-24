package org.bitmagic.lab.reycatcher;

import lombok.Value;

/**
 * @author yangrd
 */
@Value(staticConstructor = "of")
public class ReqTokenInfo {

     String type;

     String value;
}
