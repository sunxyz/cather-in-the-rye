package org.bitmagic.lab.reycatcher.impl;

import lombok.RequiredArgsConstructor;
import org.bitmagic.lab.reycatcher.*;

import java.util.Collection;
import java.util.Optional;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@RequiredArgsConstructor
public abstract class AbstractSessionManager implements SessionManager {

    private final SessionRepository repository;

    @Override
    public void save(Session session) {
        repository.save(session);
    }

    @Override
    public void remove(Session session) {
        repository.save(session);
    }

    @Override
    public Optional<Session> findBySessionId(String sessionId) {
        return repository.findBySessionId(sessionId);
    }

    @Override
    public Optional<Session> findOne(Object id, String deviceType) {
        return repository.findOne(id,deviceType);
    }

    @Override
    public Optional<Session> findByToken(SessionToken token) {
        return repository.findByToken(token);
    }

    @Override
    public Page<Session> findAll(SessionFilterInfo filterInfo, int size, int page) {
        return repository.findAll(filterInfo,size,page);
    }

    @Override
    public Collection<Session> listAll(SessionFilterInfo filterInfo) {
        return repository.listAll(filterInfo);
    }

    @Override
    public void renewal(SessionToken token) {
        repository.renewal(token);
    }

    @Override
    public void switchId(LoginInfo from, LoginInfo to) {
        repository.switchId(from,to);
    }

    @Override
    public Optional<LoginInfo> findSwitchIdTo(LoginInfo from) {
        return repository.findSwitchIdTo(from);
    }
}
