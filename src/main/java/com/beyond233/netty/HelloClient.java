package com.beyond233.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * description: hello client
 *
 * @author beyond233
 * @since 2021/5/6 21:57
 */
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1. boot class
        new Bootstrap()
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
                // 5. connect to server
                .connect(new InetSocketAddress(8080))
                // sync()为阻塞方法：直到和server建立连接后才执行
                .sync()
                // channel()方法获取连接的channel
                .channel()
                // 向server发送数据
                .writeAndFlush("hello server");
    }
}
