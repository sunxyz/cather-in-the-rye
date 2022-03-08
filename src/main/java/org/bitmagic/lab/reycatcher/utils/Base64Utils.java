package org.bitmagic.lab.reycatcher.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author yangrd
 * @date 2022/03/08
 */
public class Base64Utils {

    final static Base64.Encoder ENCODER = Base64.getEncoder();
    final static Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 给字符串加密
     */
    public static String encode(String text) {
        byte[] textByte = new byte[0];
        try {
            textByte = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  ENCODER.encodeToString(textByte);
    }

    /**
     * 将加密后的字符串进行解密
     */
    public static String decode(String encodedText) {
        try {
           return new String(DECODER.decode(encodedText), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean match(String encodedStr, String str){
        return encodedStr.equals(encode(str));
    }
}
