package com.beyond233.netty.demo.rpc;

import com.beyond233.netty.protocol.MessageCodecSharable;
import com.beyond233.netty.protocol.ProtocolFrameDecoder;
import com.beyond233.netty.protocol.message.RpcRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 模拟rpc客户端
 *
 * @author beyond233
 * @since 2021/5/23 23:36
 */
@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        // rpc响应消息处理器
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(rpcResponseMessageHandler);
                        }
                    });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();

            // 向服务端发送rpc请求消息
            RpcRequestMessage requestMessage =
                    new RpcRequestMessage(1, "com.beyond233.netty.demo.chat.service.HelloService",
                            "sayHello", String.class, new Class[]{String.class}, new Object[]{"beyond"});
            ChannelFuture channelFuture = channel
                    .writeAndFlush(requestMessage)
                    .addListener(promise -> {
                        // 消息发送成功
                        if (promise.isSuccess()) {
                            log.debug("RPC请求发送成功");
                            promise.notify();
                        } else {
                            log.debug("RPC失败，原因:", promise.cause());
                            log.debug("RPC请求发送失败，尝试再次发送");
                            channel.writeAndFlush(requestMessage);
                        }
                    });

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
