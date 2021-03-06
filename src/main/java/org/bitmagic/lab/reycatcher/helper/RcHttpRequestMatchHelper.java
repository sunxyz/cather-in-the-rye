package org.bitmagic.lab.reycatcher.helper;

import org.bitmagic.lab.reycatcher.config.InstanceHolder;
import org.bitmagic.lab.reycatcher.predicate.HttpRequestPredicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangrd
 */
public class RcHttpRequestMatchHelper {

    private static final HttpRequestPredicate PREDICATE = InstanceHolder.getInstance(HttpRequestPredicate.class);

    public static boolean allMatch(HttpServletRequest request, String... specs) {
        return allMatch(PREDICATE, request, specs);
    }

    public static boolean anyMatch(HttpServletRequest request, String... specs) {
        return anyMatch(PREDICATE, request, specs);
    }

    public static boolean noneMatch(HttpServletRequest request, String... specs) {
        return noneMatch(PREDICATE, request, specs);
    }

    public static boolean allMatch(HttpRequestPredicate predicate, HttpServletRequest request, String... specs) {
        return Arrays.stream(specs).allMatch(spec -> match(request, spec, predicate));
    }

    public static boolean anyMatch(HttpRequestPredicate predicate, HttpServletRequest request, String... specs) {
        return Arrays.stream(specs).anyMatch(spec -> match(request, spec, predicate));
    }

    public static boolean noneMatch(HttpRequestPredicate predicate, HttpServletRequest request, String... specs) {
        return Arrays.stream(specs).noneMatch(spec -> match(request, spec, predicate));
    }

    /**
     * * # 匹配规则   predicateName=k[:v][,k1[:v1]]
     * * # predicateName: Path,Cookie,Params,Header,Host,Method
     *
     * @param request
     * @param spec
     * @param predicate
     * @return
     */
    static boolean match(HttpServletRequest request, String spec, HttpRequestPredicate predicate) {
        String[] split = spec.split("=");
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
        return predicate.test(name, request, map);
    }
}
