package org.bitmagic.lab.reycatcher.predicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author yangrd
 */
public interface HttpRequestPredicate0 extends HttpRequestPredicate, BiPredicate<HttpServletRequest, Map<String,String>> {

    @Override
   default boolean test(String var1, HttpServletRequest var2, Map<String, String> var3){
        return test(var2,var3);
    }
}
