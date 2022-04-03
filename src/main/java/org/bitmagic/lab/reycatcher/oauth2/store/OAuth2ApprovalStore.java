package org.bitmagic.lab.reycatcher.oauth2.store;

import org.bitmagic.lab.reycatcher.Page;

/**
 * @author yangrd
 */
public interface OAuth2ApprovalStore {

    void addApproval(OAuth2Approval approval);

    OAuth2Approval getApproval(String userId, String clientId);

    Page<OAuth2Approval> findApprovals(String userId, int page, int size);

    void removeApproval(String userId, String clientId);

    boolean containsApproval(String userId, String clientId, String scope);
}
