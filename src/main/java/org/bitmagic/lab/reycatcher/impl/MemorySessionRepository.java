package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.Page;
import org.bitmagic.lab.reycatcher.Session;
import org.bitmagic.lab.reycatcher.SessionRepository;
import org.bitmagic.lab.reycatcher.SessionToken;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class MemorySessionRepository implements SessionRepository {

    private  final Map<SessionToken,Session> REPO = new ConcurrentHashMap<>();
    private final Map<String,SessionToken> USER2TOKEN = new ConcurrentHashMap<>();

    @Override
    public void save(Session session) {
        REPO.put(session.getSessionToken(), session);
        USER2TOKEN.put(genKey(session),session.getSessionToken());
    }

    @Override
    public void remove(Session session) {
        REPO.remove(session.getSessionToken());
        USER2TOKEN.remove(genKey(session));
    }

    @Override
    public Optional<Session> findOne(Object id, String deviceType) {
        SessionToken sessionToken = USER2TOKEN.get(genKey(id, deviceType));
        if (Objects.isNull(sessionToken)){
            return Optional.empty();
        }
        return findByToken(sessionToken);
    }

    @Override
    public Optional<Session> findByToken(SessionToken token) {
        return Optional.ofNullable(REPO.get(token));
    }

    @Override
    public Page<Session> findAll(Object filterInfo, int size, int page) {
        List<Session> sessions = new ArrayList<>(listAll(filterInfo));
        return Page.of(sessions.subList(page*size, page*size+size),sessions.size());
    }

    @Override
    public Collection<Session> listAll(Object filterInfo) {
        return REPO.values();
    }

    private String genKey(Session session){
        return genKey(session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType());
    }

    private String genKey(Object id,String deviceType){
        return String.format("%s@%s",id, deviceType);
    }
}
