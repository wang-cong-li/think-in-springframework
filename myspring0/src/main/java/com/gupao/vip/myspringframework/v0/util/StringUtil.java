package com.gupao.vip.myspringframework.v0.util;

public class StringUtil {
    public static boolean isBlank(String str) {
        if (null == str || str.length() == 0) {
            return true;
        }
        str = str.replaceAll(" ","");
        if (0 == str.length()) {
            return true;
        }
        return false;
    }
}
