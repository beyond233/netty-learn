package com.beyond233.netty.demo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 使用netty构建websocket服务端
 *
 * @author beyond233
 * @since 2021/6/4 23:27
 */
@Slf4j
public class WebSocketServer {
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
                            // 请请求和应答消息编解码为HTTP消息
                            ch.pipeline().addLast("http-codec", new HttpServerCodec());
                            // 将HTTP消息的多个部分组合成一条完整的HTTP消息
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            // ChunkedWriteHandler用来向客户端发送HTML5文件，主要支持浏览器和服务端进行WebSocket通信
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            // WebSocket消息的处理器
                            ch.pipeline().addLast("handler", new WebSocketServerHandler());
                        }
                    });

            Channel channel = serverBootstrap.bind(8080).sync().channel();
            log.info("webSocket Server 启动");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
