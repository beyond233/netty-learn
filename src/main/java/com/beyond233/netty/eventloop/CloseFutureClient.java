package com.beyond233.netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * description: 如何在channel关闭后正确地进行善后工作的处理
 *
 * @author beyond233
 * @since 2021/5/9 16:28
 */
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(8080));

        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            for (; ; ) {
                String line = scanner.nextLine();
                if ("quit".equals(line)) {
                    // close()为异步方法，close之后的代码不会马上执行
                    channel.close();
                    // 下面是错误的善后方式
                    log.debug("此处模拟关闭channel后的操作");
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "console input").start();

        // 正确善后方式1. 同步处理
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.sync();
        log.debug("此处模拟关闭channel后的操作");

        // 正确善后方式2. 异步关闭
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("此处模拟关闭channel后的操作");
                // 优雅关闭eventLoopGroup
                nioEventLoopGroup.shutdownGracefully();
            }
        });

    }
}
