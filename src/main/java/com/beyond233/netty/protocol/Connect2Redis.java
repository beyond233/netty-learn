package com.beyond233.netty.protocol;

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

import java.nio.charset.Charset;

/**
 * description: 利用nio先连接到腾讯云上的redis服务器，然后再按照redis的协议规范向redis服务器发送数据来登录redis和向redis执行命令
 *
 * @author beyond233
 * @since 2021/5/13 23:03
 */
@Slf4j
public class Connect2Redis {
    public static void main(String[] args) {
        // 13代表回车 ，10代表换行
        final byte[] LINE = {13, 10};
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
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buffer = ctx.alloc().buffer();

                                    // 1.先校验密码来登录腾讯云上的redis，校验密码命令为： auth 123456 (auth password)
                                    // *2代表auth 123456这条命令总共auth和123456两个元素
                                    buffer.writeBytes("*2".getBytes());
                                    // redis协议规定每发完一次数据后要发送一个回车和换行
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$4".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("auth".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$6".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("123456".getBytes());
                                    buffer.writeBytes(LINE);

                                    // 2.登录成功后向redis中写入命令: set name beyond233 (set key value)
                                    // *3代表set key value总共由3个元素个数
                                    buffer.writeBytes("*3".getBytes());
                                    // 每条数据之间添加回车和换行
                                    buffer.writeBytes(LINE);
                                    // $3代表命令set的长度
                                    buffer.writeBytes("$3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("set".getBytes());
                                    buffer.writeBytes(LINE);
                                    // $4代表name的长度
                                    buffer.writeBytes("$4".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("name".getBytes());
                                    buffer.writeBytes(LINE);
                                    // $8代表beyond233的长度
                                    buffer.writeBytes("$9".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("beyond233".getBytes());
                                    buffer.writeBytes(LINE);

                                    ctx.writeAndFlush(buffer);
                                }

                                /*
                                 * 这里接收redis发来的响应数据
                                 * */
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf response = (ByteBuf) msg;
                                    log.debug("redis发来的响应为：{}", response.toString(Charset.defaultCharset()));
                                }
                            });
                        }
                    });
            // 连接到腾讯云服务器上的redis
            ChannelFuture future = bootstrap.connect("121.4.160.156", 6379).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
