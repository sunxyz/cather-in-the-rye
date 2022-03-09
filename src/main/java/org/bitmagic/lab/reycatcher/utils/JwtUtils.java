package org.bitmagic.lab.reycatcher.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bitmagic.lab.reycatcher.ex.RyeCatcherException;

import java.util.Date;
import java.util.Map;

/**
 * @author yangrd
 */
public class JwtUtils {

    public static String createToken(Algorithm algorithm, String subject, long expiresAtMillisecond, Map<String, Object> claims) {
        return JWT.create()
                //签发人
                .withIssuer("rye-cather")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(expiresAtMillisecond))
                .withSubject(subject)
                //自定义信息
                .withPayload(claims)
                .sign(algorithm);
    }

    public static DecodedJWT verifierGetJwt(Algorithm algorithm, String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("rye-cather")
                .build(); //Reusable verifier instance
        try {
            return verifier.verify(token);
        }catch (JWTVerificationException jwtVerificationException){
            throw new RyeCatcherException(jwtVerificationException);
        }
    }
}
