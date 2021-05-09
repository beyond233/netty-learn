package com.beyond233.netty.bytebuf;

import com.beyond233.netty.util.LogUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * description: 用slice将byteBuf分割成多个,其和原byteBuf还是共用一块内存，
 * 类似的还有duplicate和copy（深拷贝，修改与原byteBuf都无关）
 *
 * @author beyond233
 * @since 2021/5/9 21:59
 */
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});
        LogUtil.log(buffer);

        // 利用slice将byteBuf分割成多个，切片和原有的byteBuf使用的其实还是同一块内存
        ByteBuf s1 = buffer.slice(0, 5);
        ByteBuf s2 = buffer.slice(5, 5);
        LogUtil.log(s1);
        LogUtil.log(s2);

        // 在内存中包留，不允许release
        s1.retain();
        s2.retain();


        // 切片过程没有发生数据复制
        s1.setByte(0, 'a');
        LogUtil.log(s1);
        LogUtil.log(buffer);
        s1.release();
    }
}
