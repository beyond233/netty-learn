package com.beyond233.netty.demo.chat.handler;

import com.beyond233.netty.demo.chat.service.UserServiceFactory;
import com.beyond233.netty.demo.chat.session.SessionFactory;
import com.beyond233.netty.protocol.message.LoginRequestMessage;
import com.beyond233.netty.protocol.message.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 继承SimpleChannelInboundHandler，只对指定的单一种类消息进行处理，即只处理登录请求
 *
 * @author beyond233
 * @since 2021/5/22 17:32
 */
@Slf4j
public class LoginRequestInboundHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        log.debug("登录请求--->username={},password={}", username, password);
        // 登录校验
        boolean login = UserServiceFactory.getUserService().login(username, password);
        // 响应客户端
        LoginResponseMessage response;
        if (login) {
            // 登录成功将客户端channel绑定到session中
            SessionFactory.getSession().bind(ctx.channel(), username);
            response = new LoginResponseMessage(true, "登录成功");
        } else {
            response = new LoginResponseMessage(false, "登录失败");
        }
        ctx.writeAndFlush(response);
    }
}