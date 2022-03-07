package org.bitmagic.lab.reycatcher.utils;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public class StringUtils {

    public static boolean isEmpty(String str){
        return str==null||str.trim().length()>0;
    }

    /**
     * 驼峰转 下划线
     */
    public static String toUnderlineCase(String camelCaseStr) {
        if (isEmpty(camelCaseStr)) {
            return null;
        }
        char[] charArray = camelCaseStr.toCharArray();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, l = charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                buffer.append("_").append(charArray[i] += 32);
            } else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }
}
