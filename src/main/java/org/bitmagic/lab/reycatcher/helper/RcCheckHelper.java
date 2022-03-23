package org.bitmagic.lab.reycatcher.helper;

import org.bitmagic.lab.reycatcher.MatchRelation;
import org.bitmagic.lab.reycatcher.RyeCatcher;
import org.bitmagic.lab.reycatcher.utils.ValidateUtils;


/**
 * @author yangrd
 */
public class RcCheckHelper {

    public static void checkLogin() {
        RyeCatcher.checkLogin();
    }

    public static void noCheck() {
    }

    public static void check(boolean pass, String error) {
        ValidateUtils.check(pass, error);
    }

    public static void checkRole(String... roles) {
        checkAllRole(roles);
    }

    public static void checkAllRole(String... roles) {
        RyeCatcher.check("role", MatchRelation.ALL, roles);
    }

    public static void checkAnyRole(String... roles) {
        RyeCatcher.check("role", MatchRelation.ANY, roles);
    }

    public static void checkNoneRole(String... roles) {
        RyeCatcher.check("role", MatchRelation.NONE, roles);
    }

    public static void checkPerm(String... perms) {
        checkAllPerm(perms);
    }

    public static void checkAllPerm(String... perms) {
        RyeCatcher.check("perm", MatchRelation.ALL, perms);
    }

    public static void checkAnyPerm(String... perms) {
        RyeCatcher.check("perm", MatchRelation.ANY, perms);
    }

    public static void checkNonePerm(String... perms) {
        RyeCatcher.check("perm", MatchRelation.NONE, perms);
    }
}


