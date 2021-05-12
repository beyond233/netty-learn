package com.beyond233.netty.datagram;

import com.beyond233.netty.util.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * description: 行解码器解决粘包、拆包问题
 *
 * @author beyond233
 * @since 2021/5/11 23:54
 */
public class LineClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                /**
                                 * 会在连接channel建立成功后会触发active事件
                                 * */
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    char c = '0';
                                    Random random = new Random();
                                    for (int i = 0; i < 10; i++) {
                                        buffer.writeBytes(StringUtil.makeString(c++, random.nextInt(256) + 1).getBytes());
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
