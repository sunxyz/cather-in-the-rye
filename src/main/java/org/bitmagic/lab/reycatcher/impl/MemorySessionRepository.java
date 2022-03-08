package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class MemorySessionRepository implements SessionRepository {

    private final Map<SessionToken, Session> REPO = new ConcurrentHashMap<>();
    private final Map<String, SessionToken> USER2TOKEN = new ConcurrentHashMap<>();

    {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(()->{
            long now = System.currentTimeMillis();
            if(!REPO.isEmpty()){
                REPO.values().stream().filter(session -> now-session.getLastAccessedTime()>session.getMaxInactiveInterval()).forEach(this::remove);
            }
        }, 1000, 5*60000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(Session session) {
        REPO.put(session.getSessionToken(), session);
        USER2TOKEN.put(genKey(session), session.getSessionToken());
    }

    @Override
    public void remove(Session session) {
        REPO.remove(session.getSessionToken());
        USER2TOKEN.remove(genKey(session));
    }

    @Override
    public Optional<Session> findOne(Object id, String deviceType) {
        SessionToken sessionToken = USER2TOKEN.get(genKey(id, deviceType));
        if (Objects.isNull(sessionToken)) {
            return Optional.empty();
        }
        return findByToken(sessionToken);
    }

    @Override
    public Optional<Session> findByToken(SessionToken token) {
        return Optional.ofNullable(REPO.get(token));
    }

    @Override
    public Page<Session> findAll(SessionFilterInfo filterInfo, int size, int page) {
        List<Session> sessions = new ArrayList<>(listAll(filterInfo));
        return Page.of(sessions.subList(page * size, page * size + size), sessions.size());
    }

    @Override
    public Collection<Session> listAll(SessionFilterInfo filterInfo) {
        return REPO.values().stream().filter(filter0(filterInfo)).collect(Collectors.toList());
    }

    @Override
    public void renewal(SessionToken token) {
        findByToken(token) .ifPresent(o->{
            Session.DefaultSession session = Session.from(o);
            session.setLastAccessedTime(System.currentTimeMillis());
        });
    }

    private String genKey(Session session) {
        return genKey(session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType());
    }

    private String genKey(Object id, String deviceType) {
        return String.format("%s@%s", id, deviceType);
    }

    protected Predicate<Session> filter0(SessionFilterInfo filterInfo){
            return session ->
                 (Objects.isNull(filterInfo.getLoginUserId())||session.getLoginInfo().getUserId().equals(filterInfo.getLoginUserId()))
                        &&(StringUtils.isEmpty(filterInfo.getTokenType())||session.getSessionToken().getType().contains(filterInfo.getTokenType()))
                        &&(StringUtils.isEmpty(filterInfo.getTokenValue())||session.getSessionToken().getToken().contains(filterInfo.getTokenValue()))
                        &&(StringUtils.isEmpty(filterInfo.getLoginDeviceType())||session.getLoginInfo().getDeviceType().contains(filterInfo.getLoginDeviceType()))
                        &&(Objects.isNull(filterInfo.getBeginCreationTime())||session.getCreationTime()>filterInfo.getBeginCreationTime())
                        &&(Objects.isNull(filterInfo.getEndCreationTime())||session.getCreationTime()<filterInfo.getEndCreationTime())
                        &&(Objects.isNull(filterInfo.getBeginLastAccessedTime())||session.getLastAccessedTime()>filterInfo.getBeginLastAccessedTime())
                        &&(Objects.isNull(filterInfo.getEndLastAccessedTime())||session.getLastAccessedTime()<filterInfo.getEndLastAccessedTime());

    }
}
