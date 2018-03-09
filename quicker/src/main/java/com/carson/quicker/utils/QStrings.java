package com.carson.quicker.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Created by carson on 2018/3/9.
 */

public class QStrings {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String EMPTY = "";

    public static CharSequence filter(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return EMPTY;
        } else {
            return text.toString().trim();
        }
    }

    public static CharSequence filter(CharSequence text, String flag) {
        if (TextUtils.isEmpty(text)) {
            return EMPTY;
        } else {
            return text.toString().trim().replaceAll(flag, EMPTY);
        }
    }

    /**
     * Returns true if the str is null or zero length.
     *
     * @param str the CharSequence to be examined
     * @return true if str is null or zero length
     */
    public static boolean isNotBlank(@Nullable CharSequence str) {
        return null != str && str.toString().trim().length() > 0;
    }

    /**
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
