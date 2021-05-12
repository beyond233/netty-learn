package com.beyond233.netty.util;

/**
 * description:
 *
 * @author beyond233
 * @since 2021/5/12 22:54
 */
public class StringUtil {

    public static String makeString(char c, int length) {
        StringBuilder builder = new StringBuilder(length + 2);
        for (int i = 0; i < length; i++) {
            builder.append(c);
        }
        builder.append("\n");
        return builder.toString();
    }

}
