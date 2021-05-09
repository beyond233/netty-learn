package com.beyond233.netty.bytebuf;

import com.beyond233.netty.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * description:
 *
 * @author beyond233
 * @since 2021/5/9 22:13
 */
public class TestComposite {
    public static void main(String[] args) {
        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer();
        b1.writeBytes(new byte[]{'1', '2', '3'});

        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer();
        b1.writeBytes(new byte[]{'4', '5', '6'});

//        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
//        buf.writeBytes(b1).writeBytes(b2);
//        LogUtil.log(buf);

        // composite将多个buf组合成一个，这方法底层避免了内存拷贝，即零拷贝
        CompositeByteBuf compositeBuffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeBuffer.addComponents(true, b1, b2);
        LogUtil.log(compositeBuffer);


    }
}
