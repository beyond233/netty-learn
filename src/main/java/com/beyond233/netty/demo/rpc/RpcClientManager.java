package com.beyond233.netty.demo.rpc;

import com.beyond233.netty.protocol.MessageCodecSharable;
import com.beyond233.netty.protocol.ProtocolFrameDecoder;
import com.beyond233.netty.protocol.message.RpcRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * description: rpc客户端管理器
 *
 * @author beyond233
 * @since 2021/5/27 23:05
 */
public class RpcClientManager {

    private static Channel channel = null;
    /**
     * sync lock
     **/
    private static final Object LOCK = new Object();

    /**
     * DCL实现singleton
     **/
    public static Channel getChannel(int port) {
        if (channel != null) { // t1
            return channel;
        }
        synchronized (LOCK) { // t1 lock
            if (channel != null) {
                return channel;
            }
            initChannel(port);
            return channel;
        }
    }

    /**
     * 初始化channel (只执行一次，保证单例channel)
     **/
    private static void initChannel(int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

        // 构造bootstrap
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(loggingHandler);
                        ch.pipeline().addLast(messageCodec);
                        ch.pipeline().addLast(rpcResponseMessageHandler);
                    }
                });

        try {
            // 同步等待，直到获取到连接成功后的channel
            channel = bootstrap.connect(new InetSocketAddress(port)).sync().channel();
            // 异步关闭future
            channel.closeFuture().addListener(future -> group.shutdownGracefully());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getChannel(8080).writeAndFlush(new RpcRequestMessage(1, "com.beyond233.netty.demo.chat.service.HelloService",
                "sayHello", String.class, new Class[]{String.class}, new Object[]{"beyond"}));

    }
}
