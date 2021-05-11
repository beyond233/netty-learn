package com.beyond233.netty.datagram;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

import static com.beyond233.netty.util.ByteUtil.bytesToString;
import static com.beyond233.netty.util.ByteUtil.fillBytes;

/**
 * description: 定长解码器解决粘包、拆包问题
 *
 * @author beyond233
 * @since 2021/5/11 22:50
 */
@Slf4j
public class FixedClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                /**
                                 * 会在连接channel建立成功后触发active事件
                                 */
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    char c = '0';
                                    Random random = new Random();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] bytes = fillBytes(c++, random.nextInt(10) + 1);
                                        buffer.writeBytes(bytes);
                                        log.debug("send : {}", bytesToString(bytes));
                                    }
                                    ctx.writeAndFlush(buffer);
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(8080)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }

    }


}

