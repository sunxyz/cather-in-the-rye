package org.bitmagic.lab.reycatcher.reqmatches;

public interface ReqMatchesFunc {

    void stopNextMatch();

    void stopAllMatch();

    void returnRes(String o);

    String getReturnRes();

    void restReturnRes();
}
