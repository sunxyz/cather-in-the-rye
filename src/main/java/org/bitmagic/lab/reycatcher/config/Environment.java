package org.bitmagic.lab.reycatcher.config;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bitmagic.lab.reycatcher.AuthMatchInfoProvider;
import org.bitmagic.lab.reycatcher.RyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.SessionManager;
import org.bitmagic.lab.reycatcher.impl.DefaultRyeCatcherActionListener;
import org.bitmagic.lab.reycatcher.predicate.CompositeHttpRequestPredicate;
import org.bitmagic.lab.reycatcher.predicate.HttpRequestPredicate;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yangrd
 */
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Environment {

    @Getter
    private final SessionManager sessionManager;

    @Getter
    private final AuthMatchInfoProvider authMatchInfoProvider;

    @Getter
    @Setter
    private RyeCatcherActionListener ryeCatcherActionListener = new DefaultRyeCatcherActionListener();

    @Getter
    @Setter
    private Algorithm algorithm;

    private Set<HttpRequestPredicate> httpRequestPredicates = new HashSet<>();

    public void addHttpRequestPredicate(HttpRequestPredicate predicate) {
        httpRequestPredicates.add(predicate);
    }

    public HttpRequestPredicate getHttpRequestPredicate() {
        return new CompositeHttpRequestPredicate(this.httpRequestPredicates);
    }



}
