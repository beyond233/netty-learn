package com.beyond233.netty.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * description: 通过包装LengthFieldBasedFrameDecoder来解码自定义协议
 *
 * @author beyond233
 * @since 2021/5/22 15:51
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
