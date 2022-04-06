package org.bitmagic.lab.reycatcher.impl;

import org.bitmagic.lab.reycatcher.*;
import org.bitmagic.lab.reycatcher.config.DynamicRcConfigHolder;
import org.bitmagic.lab.reycatcher.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class MemorySessionRepository implements SessionRepository {

    private final Map<String, Session> REPO = new ConcurrentHashMap<>();
    private final Map<SessionToken, String> TOKEN2ID = new ConcurrentHashMap<>();
    private final Map<String, String> USER2ID = new ConcurrentHashMap<>();
    private final Map<LoginInfo, LoginInfo> SWITCH_ID = new ConcurrentHashMap<>();

    {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            if (!REPO.isEmpty()) {
                REPO.values().stream().filter(session -> now - session.getLastAccessedTime() > session.getMaxInactiveInterval()).forEach(this::remove);
            }
        }, 1000, 5 * 60000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(Session session) {
        REPO.put(session.getId(), session);
        TOKEN2ID.put(session.getSessionToken(), session.getId());
        USER2ID.put(genKey(session), session.getId());
    }

    @Override
    public void remove(Session session) {
        REPO.remove(session.getId());
        TOKEN2ID.remove(session.getSessionToken());
        USER2ID.remove(genKey(session));
        SWITCH_ID.remove(session.getLoginInfo());
    }

    @Override
    public Optional<Session> findBySessionId(String sessionId) {
        return Optional.ofNullable(REPO.get(sessionId));
    }

    @Override
    public Optional<Session> findByLoginInfo(Object id, String deviceType) {
        String sessionId = USER2ID.get(genKey(id, deviceType));
        if (Objects.isNull(sessionId)) {
            return Optional.empty();
        }
        return findBySessionId(sessionId);
    }

    @Override
    public Optional<Session> findByToken(SessionToken token) {
        String sessionId = TOKEN2ID.get(token);
        if (Objects.isNull(sessionId)) {
            return Optional.empty();
        }
        return findBySessionId(sessionId);
    }

    @Override
    public Page<Session> findAll(SessionFilterInfo filterInfo, int size, int page) {
        List<Session> sessions = new ArrayList<>(listAll(filterInfo));
        return Page.of(page * size > sessions.size() ? Collections.emptyList() : sessions.subList(page * size, Math.min(page * size + size, sessions.size())), sessions.size());
    }

    @Override
    public Collection<Session> listAll(SessionFilterInfo filterInfo) {
        return REPO.values().stream().filter(filter0(filterInfo)).collect(Collectors.toList());
    }

    @Override
    public void renewal(SessionToken token) {
        findByToken(token).ifPresent(o -> {
            Session.DefaultSession session = Session.from(o);
            session.setLastAccessedTime(System.currentTimeMillis());
        });
    }

    @Override
    public void switchId(LoginInfo from, LoginInfo to) {
        if (Objects.isNull(to)) {
            SWITCH_ID.remove(from);
        } else {
            SWITCH_ID.put(from, to);
        }
    }

    @Override
    public Optional<LoginInfo> findSwitchIdTo(LoginInfo from) {
        return Optional.ofNullable(SWITCH_ID.get(from));
    }

    private String genKey(Session session) {
        return genKey(session.getLoginInfo().getUserId(), session.getLoginInfo().getDeviceType());
    }

    private String genKey(Object id, String deviceType) {
        return String.format("%s@%s@%s", DynamicRcConfigHolder.getCertificationSystemId(), id, deviceType);
    }

    protected Predicate<Session> filter0(SessionFilterInfo filterInfo) {
        return session ->
                Objects.isNull(filterInfo) ||
                        ((Objects.isNull(filterInfo.getLoginUserId()) || session.getLoginInfo().getUserId().equals(filterInfo.getLoginUserId()))
                                && (StringUtils.isBlank(filterInfo.getTokenType()) || session.getSessionToken().getGenType().contains(filterInfo.getTokenType()))
                                && (StringUtils.isBlank(filterInfo.getTokenValue()) || session.getSessionToken().getToken().contains(filterInfo.getTokenValue()))
                                && (StringUtils.isBlank(filterInfo.getLoginDeviceType()) || session.getLoginInfo().getDeviceType().contains(filterInfo.getLoginDeviceType()))
                                && (Objects.isNull(filterInfo.getBeginCreationTime()) || session.getCreationTime() > filterInfo.getBeginCreationTime())
                                && (Objects.isNull(filterInfo.getEndCreationTime()) || session.getCreationTime() < filterInfo.getEndCreationTime())
                                && (Objects.isNull(filterInfo.getBeginLastAccessedTime()) || session.getLastAccessedTime() > filterInfo.getBeginLastAccessedTime())
                                && (Objects.isNull(filterInfo.getEndLastAccessedTime()) || session.getLastAccessedTime() < filterInfo.getEndLastAccessedTime()));

    }
}
