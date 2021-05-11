package com.beyond233.netty.util;

/**
 * description: byte util
 *
 * @author beyond233
 * @since 2021/5/11 23:57
 */
public class ByteUtil {

    /**
     * 输出一个定长为10，指定个数字符，剩余位由‘_’填充的byte数组
     *
     * @param c      字符
     * @param length 字符个数
     * @return {@link byte[]}
     * @since 2021/5/11 23:10
     */
    public static byte[] fillBytes(char c, int length) {
        // 定长十位
        int fixLength = 10;
        byte[] bytes = new byte[fixLength];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) c;
        }

        for (int i = length; i < fixLength; i++) {
            bytes[i] = '_';
        }

        return bytes;
    }

    /**
     * 打印byte数组
     *
     * @since 2021/5/11 23:14
     */
    public static void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print((char) b);
        }
        System.out.println();
    }


    /**
     * byte数组转为String
     */
    public static String bytesToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append((char) b);
        }
        return builder.toString();
    }

}
