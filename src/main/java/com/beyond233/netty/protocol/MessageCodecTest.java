package com.beyond233.netty.protocol;

import com.beyond233.netty.protocol.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * description:
 *
 * @author beyond233
 * @since 2021/5/14 22:17
 */
public class MessageCodecTest {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                // 在我们自定义的消息协议中，总长为16字节，所以这里可用定长帧解码器来解决粘包、半包问题
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(),
                new MessageCodec());

        LoginRequestMessage message = new LoginRequestMessage("beyond", "123456");
        // 1.消息出站时，将被MessageCodec编码为byteBuf
        channel.writeOutbound(message);

        // 2.入站则被解码
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // 入站前先手动把消息编码下
        new MessageCodec().encode(null, message, byteBuf);
        channel.writeInbound(byteBuf);


    }
}
