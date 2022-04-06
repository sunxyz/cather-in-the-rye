package org.bitmagic.lab.reycatcher.config;

import com.auth0.jwt.algorithms.Algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class DynamicRcConfigHolder {

    private static final Map<String, Algorithm> SYS_ID_2_ALGORITHM_CACHE = new HashMap<>();
    public static Supplier<CertificationSystemDefine> delegate;

    public static String getCertificationSystemId() {
        return getConfigInfo().getId();
    }

    public static String getGenTokenType() {
        return getConfigInfo().getGenTokenType();
    }

    public static String getOutClientTokenName() {
        return getConfigInfo().getOutClientTokenName();
    }

    public static int getSessionTimeoutMillisecond() {
        return getConfigInfo().getSessionTimeOutMillisecond();
    }

    public static List<String> listCertificationSystemPredicate(){
        return getConfigInfo().getPredicates();
    }

    public static boolean isNeedSave() {
        return getConfigInfo().isSessionNeedSave();
    }

    public static boolean isNeedOutClient() {
        return getConfigInfo().isTokenNeedOutClient();
    }

    public static boolean isSameDriveMutex() {
        return getConfigInfo().isSameDriveMutex();
    }

    public static boolean isEnableJwtAuthMatchInfoPayload(){
        return getConfigInfo().isEnableJwtAuthMatchInfoPayload();
    }

    public static String getLoginPage() {
        return getConfigInfo().getLoginPage();
    }

    public static Algorithm getAlgorithm() {
        return SYS_ID_2_ALGORITHM_CACHE.computeIfAbsent(getCertificationSystemId(), k -> {
            Algorithm instance = InstanceHolder.getInstance(Algorithm.class);
            return Objects.isNull(instance) ? Algorithm.HMAC512(getConfigInfo().getJwtHmacSecret()) : instance;
        });
    }

    private static CertificationSystemDefine getConfigInfo() {
        return delegate.get();
    }

}
