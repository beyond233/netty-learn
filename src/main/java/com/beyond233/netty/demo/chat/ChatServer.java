package com.beyond233.netty.demo.chat;

import com.beyond233.netty.demo.chat.handler.ChatRequestInboundHandler;
import com.beyond233.netty.demo.chat.handler.GroupCreateRequestInboundHandler;
import com.beyond233.netty.demo.chat.handler.LoginRequestInboundHandler;
import com.beyond233.netty.protocol.MessageCodecSharable;
import com.beyond233.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 聊天服务器
 *
 * @author beyond233
 * @since 2021/5/15 17:26
 */
@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        final LoginRequestInboundHandler loginRequestInboundHandler = new LoginRequestInboundHandler();
        ChatRequestInboundHandler chatRequestInboundHandler = new ChatRequestInboundHandler();
        GroupCreateRequestInboundHandler groupCreateRequestInboundHandler = new GroupCreateRequestInboundHandler();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 空闲状态监测处理器: 判断读、写的空闲时间是不是过长，以此来判断服务端和客户端的连接是不是已经断开
                            // 5s内channel若没有收到数据就会触发一个IdleState#READER_IDLE事件
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            // 使用ChannelDuplexHandler来处理IdleState#READER_IDLE事件，其可以处理出站又可以处理出站数据
                            ch.pipeline().addLast(new ChannelDuplexHandler() {
                                /**
                                 * 用来触发特殊事件
                                 **/
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                                    // 触发了读空闲事件
                                    if (IdleState.READER_IDLE.equals(idleStateEvent.state())) {
                                        log.debug("服务端已经5s没有收到数据了");

                                    }
                                }
                            });
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodecSharable);
                            ch.pipeline().addLast(loginRequestInboundHandler);
                            ch.pipeline().addLast(chatRequestInboundHandler);
                            ch.pipeline().addLast(groupCreateRequestInboundHandler);
                        }
                    });
            Channel channel = bootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
