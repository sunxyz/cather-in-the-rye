package org.bitmagic.lab.reycatcher.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.bitmagic.lab.reycatcher.config.spring.RyeCatcherProperties;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class ConfigHolder {

    public static Supplier<RyeCatcherProperties.CertificationSystemInfo> delegate;

    public static String getRyeCatcherPath() {
        return delegate.get().getRyeCatcherPath();
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

    public static boolean isNeedSave() {
        return getConfigInfo().isSessionNeedSave();
    }

    public static boolean isNeedOutClient() {
        return getConfigInfo().isSessionNeedOutClient();
    }

    public static boolean isLoginMutex() {
        return getConfigInfo().isLoginMutex();
    }

    public static Algorithm getAlgorithm() {
        Algorithm instance = InstanceHolder.getInstance(Algorithm.class);
        return Objects.isNull(instance) ? Algorithm.HMAC512(getConfigInfo().getJwtHmacSecret()) : instance;
    }

    private static RyeCatcherProperties.CertificationSystemInfo getConfigInfo() {
        return delegate.get();
    }

}
