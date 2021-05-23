package com.beyond233.netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * description: 使用Option对连接进行优化配置
 *
 * @author beyond233
 * @since 2021/5/23 15:15
 */
public class TestOptionClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    // 3s内没有连接成功就抛出超时异常
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    // 发送数据时不开启nagle算法，即数据即时发送不会有延迟
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(8080));
            Channel channel = future.sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
