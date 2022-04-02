package org.bitmagic.lab.reycatcher.oauth2.model;

import lombok.Data;

/**
 * @author yangrd
 */
@Data
public class ResponseErrorInfo {
    private String error;
    private String errorDescription;
    private String errorUri;

}
