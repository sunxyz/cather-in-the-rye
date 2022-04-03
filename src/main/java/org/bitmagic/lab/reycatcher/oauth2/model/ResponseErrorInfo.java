package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangrd
 */
@Data
@Builder
public class ResponseErrorInfo {
    private String error;
    private String errorDescription;
    private String errorUri;

}
