package org.bitmagic.lab.reycatcher.config;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.bitmagic.lab.reycatcher.AuthMatchInfoProvider;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.helper.RcHttpRequestMatchHelper;
import org.bitmagic.lab.reycatcher.predicate.HttpRequestPredicate;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */

public interface RyeCatcherBootstrap {

    static RyeCatcherBootstrap getInstance() {
        return DefaultRyeCatcherBootstrap.INSTANCE;
    }

    void init(Configuration config);

    @Slf4j
    class DefaultRyeCatcherBootstrap implements RyeCatcherBootstrap {

        private static final CertificationSystemDefine DEFAULT_CERTIFICATION_SYSTEM_INFO = CertificationSystemDefine.of("default-id", Collections.emptyList(), SessionToken.GenTypeCons.SESSION_ID, null, false, "JSESSIONID", 30 * 60 * 1000, true, true, true);
        static RyeCatcherBootstrap INSTANCE = new DefaultRyeCatcherBootstrap();

        @Override
        public void init(Configuration config) {

            Map<Class<?>, Object> beans = new HashMap<>(16);
            Environment environment = config.getEnvironment();
            beans.put(SessionManager.class, environment.getSessionManager());
            beans.put(AuthMatchInfoProvider.class, environment.getAuthMatchInfoProvider());
            beans.put(RyeCatcherActionListener.class, environment.getRyeCatcherActionListener());
            beans.put(Algorithm.class, environment.getAlgorithm());
            beans.put(HttpRequestPredicate.class, environment.getCertificationSystemPredicate());
            InstanceHolder.delegate = beans::get;

            DynamicRcConfigHolder.delegate = () -> {
                if (Objects.isNull(config.getCertificationSystemDefines())) {
                    return DEFAULT_CERTIFICATION_SYSTEM_INFO;
                }
                HttpServletRequest request = RcRequestContextHolder.getContext().getRequest();
                List<CertificationSystemDefine> systemDefines = config.getCertificationSystemDefines().stream().filter(o -> mathCertificationSystemDefine(o, environment.getCertificationSystemPredicate(), request)).collect(Collectors.toList());
                if (systemDefines.size() != 1) {
                    throw new IllegalStateException("systemDefines == 1");
                }
                return systemDefines.iterator().next();
            };
            log.info("RyeCatcher init success");
        }

        private boolean mathCertificationSystemDefine(CertificationSystemDefine certificationSystemDefine, HttpRequestPredicate httpRequestPredicate, HttpServletRequest request) {
            return certificationSystemDefine.getPredicates().stream().allMatch(specStr -> RcHttpRequestMatchHelper.match(request, specStr, httpRequestPredicate));
        }
    }
}
