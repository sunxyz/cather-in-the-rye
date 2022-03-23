package org.bitmagic.lab.reycatcher;

import java.lang.annotation.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CheckRole {

    String[] value();

    MatchRelation matchRelation() default MatchRelation.ANY;
}
