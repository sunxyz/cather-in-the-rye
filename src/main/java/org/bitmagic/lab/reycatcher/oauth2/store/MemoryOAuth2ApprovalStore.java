package org.bitmagic.lab.reycatcher.oauth2.store;

import org.bitmagic.lab.reycatcher.Page;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */
public class MemoryOAuth2ApprovalStore implements OAuth2ApprovalStore {

    private final Map<String, OAuth2Approval> approvals = new ConcurrentHashMap<>(32);

    @Override
    public void addApproval(OAuth2Approval approval) {
        approvals.put(genKey(approval.getClientId(), approval.getUserId()), approval);
    }

    @Override
    public OAuth2Approval getApproval(String userId, String clientId) {
        return approvals.get(genKey(clientId, userId));
    }

    @Override
    public Page<OAuth2Approval> findApprovals(String userId, int page, int size) {
        List<OAuth2Approval> list = approvals.values().stream().filter(o -> o.getUserId().equals(userId)).collect(Collectors.toList());
        return Page.of(page * size > list.size() ? Collections.emptyList() : list.subList(page * size, Math.min(page * size + size, list.size())), list.size());
    }

    @Override
    public void removeApproval(String userId, String clientId) {
        approvals.remove(genKey(clientId, userId));
    }

    @Override
    public boolean containsApproval(String userId, String clientId, String scope) {
        return approvals.containsKey(genKey(clientId, userId)) && approvals.get(genKey(clientId, userId)).getScope().contains(scope);
    }

    private String genKey(String clientId, String userId) {
        return clientId + ":" + userId;
    }
}
