package com.beyond233.netty.demo.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 处理rpc响应消息
 *
 * @author beyond233
 * @since 2021/5/23 23:40
 */
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("服务器响应:{}", msg);
    }
}
