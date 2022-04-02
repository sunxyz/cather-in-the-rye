package org.bitmagic.lab.reycatcher.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.bitmagic.lab.reycatcher.AuthMatchInfoProvider;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.SessionToken;
import org.bitmagic.lab.reycatcher.predicates.CertificationSystemPredicate;
import org.bitmagic.lab.reycatcher.support.RcRequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangrd
 */
public interface RyeCatcherBootstrap {

    static RyeCatcherBootstrap getInstance() {
        return SimpleRyeCatcherBootstrap.INSTANCE;
    }

    void init(Configuration config);

    class SimpleRyeCatcherBootstrap implements RyeCatcherBootstrap {

       static RyeCatcherBootstrap INSTANCE = new SimpleRyeCatcherBootstrap();

        private static final CertificationSystemDefine DEFAULT_CERTIFICATION_SYSTEM_INFO = CertificationSystemDefine.of("default-id", Collections.emptyList(), SessionToken.GenTypeCons.SESSION_ID, null, false, "JSESSIONID", 30 * 60 * 1000, true, true, true);


        @Override
        public void init(Configuration config) {

            Map<Class<?>, Object> beans = new HashMap<>(16);
            Environment environment = config.getEnvironment();
            beans.put(SessionManager.class, environment.getSessionManager());
            beans.put(AuthMatchInfoProvider.class, environment.getAuthMatchInfoProvider());
            beans.put(RyeCatcherActionListener.class, environment.getRyeCatcherActionListener());
            beans.put(Algorithm.class, environment.getAlgorithm());
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
        }

        private boolean mathCertificationSystemDefine(CertificationSystemDefine certificationSystemDefine, CertificationSystemPredicate certificationSystemPredicate, HttpServletRequest request) {
            return certificationSystemDefine.getPredicates().stream().allMatch(t -> {
//                    Path=k:v,k1:v1
                String[] split = t.split("=");
                String name = split[0];
                String values = split[1];
                String[] kvs = null;
                if (values.contains(",")) {
                    kvs = values.split(",");
                } else {
                    kvs = new String[]{values};
                }
                Map<String, String> map = new HashMap<>();
                for (String kv : kvs) {
                    if (kv.contains(":")) {
                        String[] kv0 = kv.split(":");
                        map.put(kv0[0], kv0[1]);
                    } else {
                        map.put(kv, "true");
                    }
                }
                return certificationSystemPredicate.test(name, request, map);
            });
        }
    }
}
