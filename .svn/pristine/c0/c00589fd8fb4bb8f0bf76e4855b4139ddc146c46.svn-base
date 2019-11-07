package com.monitor.core.tools;


public abstract class StringUtils {

    public static boolean hasLength(String str) {
        return ((str != null) && (str.length() > 0));
    }

    public static boolean hasText(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0))
            return false;
        for (int i = 0; i < strLen; ++i)
            if (!(Character.isWhitespace(str.charAt(i))))
                return true;
        return false;
    }
}