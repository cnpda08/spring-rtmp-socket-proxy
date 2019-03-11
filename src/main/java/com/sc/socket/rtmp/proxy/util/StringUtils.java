package com.sc.socket.rtmp.proxy.util;


/**
 * Created by Administrator on 2017/8/15.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 首字母小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 首字母大写
     *
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * 某字符串包含另一个字符串的个数
     *
     * @param str1 str2
     * @return
     */
    public static int stringNumbers(String str1, String str2, int counter) {
        if (str1.indexOf(str2) == -1) {
            return counter;
        } else {
            return stringNumbers(str1.substring(0, str1.lastIndexOf(str2)), str2, counter + 1);
        }
    }

}
