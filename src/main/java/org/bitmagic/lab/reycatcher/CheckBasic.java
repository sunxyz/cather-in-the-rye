package org.bitmagic.lab.reycatcher;

import java.lang.annotation.*;

/**
 * @author yangrd
 * @date 2022/03/08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface CheckBasic {

     String value();

     String realm() default "realm";

}
