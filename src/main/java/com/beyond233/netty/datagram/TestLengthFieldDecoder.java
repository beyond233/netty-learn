package com.beyond233.netty.datagram;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: LengthFieldBasedFrameDecoder解决粘包、拆包问题
 *
 * @author beyond233
 * @since 2021/5/12 23:27
 */
@Slf4j
public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        LengthFieldBasedFrameDecoder lengthDecoder =
                //  lengthAdjustment表示跳过多少个字节是真正的消息内容 ; initialByteToStrip-去除前面多少个字节
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 0);
        StringDecoder stringDecoder = new StringDecoder();
        ChannelInboundHandlerAdapter handlerAdapter = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug((String) msg);
            }
        };
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(loggingHandler, lengthDecoder, stringDecoder, handlerAdapter);

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "hello, world");
        send(buffer, "hi");
        embeddedChannel.writeInbound(buffer);
    }

    public static void send(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        buffer.writeInt(length);
        buffer.writeBytes(bytes);
    }
}
