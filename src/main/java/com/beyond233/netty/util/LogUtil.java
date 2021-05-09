package com.beyond233.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.StringUtil;


/**
 * description: log bytebuf
 *
 * @author beyond233
 * @since 2021/5/9 20:38
 */
public class LogUtil {
    public static void log(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder s = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(byteBuf.readerIndex())
                .append(" write index:").append(byteBuf.writerIndex())
                .append(" capacity:").append(byteBuf.capacity())
                .append(StringUtil.NEWLINE);
        ByteBufUtil.appendPrettyHexDump(s, byteBuf);
        System.out.println(s);
    }
}
