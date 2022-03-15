package org.bitmagic.lab.reycatcher.urimatches;

public interface UriMatchesFunc {

    void stopNextMatch();

    void stopAllMatch();

    void returnRes(String o);

    String getReturnRes();

    void restReturnRes();
}
