package com.beyond233.netty.bytebuf;

import com.beyond233.netty.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * description: learn Bytebuf
 *
 * @author beyond233
 * @since 2021/5/9 20:33
 */
public class TestBytebuf {
    public static void main(String[] args) {
        // 默认创建基于池化直接内存的byteBuf
        System.out.println(ByteBufAllocator.DEFAULT.buffer().getClass());
        // 创建基于池化堆内存的byteBuf
        System.out.println(ByteBufAllocator.DEFAULT.heapBuffer().getClass());


        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        LogUtil.log(byteBuf);

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            s.append("a");
        }

        byteBuf.writeBytes(s.toString().getBytes());
        LogUtil.log(byteBuf);

        // writeInt()为大端写入，writeIntLE()为小端写入
        byteBuf.writeInt(1);
        LogUtil.log(byteBuf);





    }
}
