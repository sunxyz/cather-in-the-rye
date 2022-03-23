package org.bitmagic.lab.reycatcher.utils;

import java.util.UUID;

/**
 * @author yangrd
 */
public class IdGenerator {

    public static String genUuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
