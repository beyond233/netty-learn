package com.beyond233.netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * description: hello client
 *
 * @author beyond233
 * @since 2021/5/6 21:57
 */
@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1. boot class
        ChannelFuture channelFuture = new Bootstrap()
                // 2. add EventLoop
                .group(new NioEventLoopGroup())
                // 3. select impl of channel
                .channel(NioSocketChannel.class)
                // 4. add handler
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    /**
                     * 此方法在和server建立连接后才被调用
                     * */
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 转换 String 为 ByteBuf
                        nsc.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5. connect to server（异步非阻塞，main线程发起了调用，真正执行connect的是nio线程）
                .connect(new InetSocketAddress(8080));

        // 6.1 使用sync()同步处理结果，其为阻塞方法：线程会在此处暂停，直到和server成功建立连接后才恢复执行
        channelFuture.sync();
        // channel()方法获取连接的channel，只有等sync()执行完才能拿到有效的channel，进而才能向服务端读写数据
        Channel channel = channelFuture.channel();
        // 由main线程输出日志
        log.debug(channel.toString());
        // 向channel写数据并向立刻向server发出
        channel.writeAndFlush("hello server (sync message)");

        // 6.2 除了sync同步处理外，还可以使用addListener(回调对象)来异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            /**
             * 在nio线程建立好连接后，会调用operationComplete()
             * */
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                // 由nio线程输出日志
                log.debug(channel.toString());
                channel.writeAndFlush("hello server (async message)");
            }
        });

    }
}
