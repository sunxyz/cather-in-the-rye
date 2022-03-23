package org.bitmagic.lab.reycatcher;

import java.lang.annotation.*;

/**
 * @author yangrd
 * @date 2022/03/04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CheckPermission {

    String[] value();

    MatchRelation matchRelation() default MatchRelation.ANY;

    // CheckPermission 使用频率较高所以加这个上了
    CheckRole or() default @CheckRole({});
}
