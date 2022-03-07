package org.bitmagic.lab.reycatcher;

public interface Config {

    static String getGenTokenType(){
        return SessionToken.TokenTypeCons.COOKIE;
    }

    static String getTokenName(){return "Authorization";}

    static long getSessionTimeOutMillisecond(){return 30*60*1000;}

    static boolean isNeedSave(String tokenType){
        return SessionToken.TokenTypeCons.COOKIE.equals(tokenType);
    }

    static boolean isNeedOutClient(String tokenType){
        return SessionToken.TokenTypeCons.COOKIE.equals(tokenType);
    }
}
