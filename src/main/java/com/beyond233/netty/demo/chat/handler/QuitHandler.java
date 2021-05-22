package com.beyond233.netty.demo.chat.handler;

import com.beyond233.netty.demo.chat.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 处理连接断开
 *
 * @author beyond233
 * @since 2021/5/22 20:06
 */
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {
    /**
     * 连接正常断开触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("连接已经断开：{}", ctx.channel());
    }

    /**
     * 发生异常时触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("连接异常断开：{}, 异常原因：{}", ctx.channel(), cause.getMessage());
    }
}
