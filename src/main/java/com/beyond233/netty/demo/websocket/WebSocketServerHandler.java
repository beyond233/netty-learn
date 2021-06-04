package com.beyond233.netty.demo.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.extern.slf4j.Slf4j;

/**
 * description: webSocket客户端消息处理器
 *
 * @author beyond233
 * @since 2021/6/4 23:32
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
