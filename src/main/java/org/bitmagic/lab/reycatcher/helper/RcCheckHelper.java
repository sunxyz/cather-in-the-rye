package org.bitmagic.lab.reycatcher.helper;

import org.bitmagic.lab.reycatcher.MatchRelation;
import org.bitmagic.lab.reycatcher.RyeCatcher;


/**
 * @author yangrd
 */
public class RcCheckHelper {

    public static void checkLogin(){
        RyeCatcher.checkLogin();
    }

    public static void noCheck(){
    }

    public static void hasRole(String... roles){
        RyeCatcher.check("role", MatchRelation.ALL, roles);
    }

    public static void anyRole(String... roles){
        RyeCatcher.check("role", MatchRelation.ANY, roles);
    }

    public static void noneRole(String... roles){
        RyeCatcher.check("role", MatchRelation.NONE, roles);
    }

    public static void hasPerm(String... roles){
        RyeCatcher.check("perm", MatchRelation.ALL, roles);
    }

    public static void anyPerm(String... roles){
        RyeCatcher.check("perm", MatchRelation.ANY, roles);
    }

    public static void nonePerm(String... roles){
        RyeCatcher.check("perm", MatchRelation.NONE, roles);
    }
}


