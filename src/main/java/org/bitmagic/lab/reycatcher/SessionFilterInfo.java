package org.bitmagic.lab.reycatcher;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangrd
 * @date 2022/03/08
 */
@Data
public class SessionFilterInfo {

    private String tokenType;

    private String tokenValue;

    private Object loginUserId;

    private String loginDeviceType;

    private Long beginCreationTime;

    private Long endCreationTime;

    private Long beginLastAccessedTime;

    private Long endLastAccessedTime;

    private Map<String,Object> meta;

    public void addAttr(String key, Object val){
        if(Objects.isNull(meta)){
            synchronized (this){
                if(Objects.isNull(meta)){
                    meta = new HashMap<>();
                }
            }
        }
        meta.put(key,val);
    }

    public Object getAttr(String key){
        return meta.get(key);
    }

    public String getAttrString(String key){
        return getT(getAttr(key),String.class);
    }

    public Integer getAttrInteger(String key){
        return getT(getAttr(key),Integer.class);
    }

    public Boolean getAttrBoolean(String key){
        return getT(getAttr(key),Boolean.class);
    }

    private static <T> T getT(Object v, Class<T> tClass){
       if( tClass.isAssignableFrom(v.getClass())){
           return (T) v;
       }else if(tClass.equals(String.class)){
           return (T)v.toString();
       }else if(tClass.equals(Integer.class)){
           return (T)Integer.valueOf(v.toString());
       }else if (tClass.equals(Boolean.class)){
           return (T) Boolean.valueOf(v.toString());
       }else {
           throw new RuntimeException("not support convert");
       }
    }
}
