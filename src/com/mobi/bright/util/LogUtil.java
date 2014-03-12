package com.mobi.bright.util;


public final class LogUtil {
    public static String createTag(final Class clazz) {
        return "MobiFuse-" + clazz.getName();
    }

}
