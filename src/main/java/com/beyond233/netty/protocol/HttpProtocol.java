package com.beyond233.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * description: netty提供了http协议的编解码实现HttpServerCodec
 *
 * @author beyond233
 * @since 2021/5/14 0:01
 */
@Slf4j
public class HttpProtocol {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            // http协议的编解码器
                            ch.pipeline().addLast(new HttpServerCodec());
//                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                                @Override
//                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                    log.debug("类对象信息：{}", msg.getClass());
//
//                                    // 请求行。请求头
//                                    if (msg instanceof HttpRequest) {
//
//                                        // 请求体
//                                    } else if (msg instanceof HttpContent) {
//
//                                    }
//                                }
//                            });

                            // SimpleChannelInboundHandler<HttpRequest>表示只在处理对象为httpRequest时才执行下面handler代码
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    log.debug("请求路径：{}", msg.uri());
                                    // 给客户端返回响应
                                    DefaultFullHttpResponse response =
                                            new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                    byte[] data = "<h1><i>hello world !</i></h1>".getBytes();
                                    response.content().writeBytes(data);
                                    // 设置响应体长度
                                    response.headers().setInt(CONTENT_LENGTH, data.length);

                                    ctx.writeAndFlush(response);

                                }
                            });

                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
