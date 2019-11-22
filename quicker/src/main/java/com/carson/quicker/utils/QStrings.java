package com.carson.quicker.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by carson on 2018/3/9.
 */

public class QStrings {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String EMPTY = "";

    public static CharSequence filter(String text) {
        if (isEmpty(text) || "null".equalsIgnoreCase(text)) {
            return EMPTY;
        } else {
            return text.trim();
        }
    }

    public static CharSequence filter(CharSequence text, String replace) {
        if (TextUtils.isEmpty(text)) {
            return EMPTY;
        } else {
            return text.toString().trim().replaceAll(replace, EMPTY);
        }
    }

    /**
     * Returns true if the str is null or zero length.
     *
     * @param obj
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else {
            return obj instanceof Map ? ((Map) obj).isEmpty() : false;
        }
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 是否是整数或者小数
     * 正则表达式说明:
     * 匹配前面子表达式零次或一次》?;
     * 匹配前面子表达式零次或多次》*;
     * 匹配前面子表达式一次或多次》+;
     * 匹配前面子表达式指定N次》 {n};
     * 匹配前面子表达式至少N次》 {n,};
     * 匹配前面子表达式指定N到M次》 {n,m};
     */
    public static boolean isDigits(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return Pattern.matches("^[0-9]+(.[0-9]+)?$", text);
    }

    public static boolean isPhone(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Patterns.PHONE.matcher(str).find();
    }

    public static boolean isEmail(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(str).find();
    }

}
