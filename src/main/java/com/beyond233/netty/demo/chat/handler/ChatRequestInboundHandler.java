package com.beyond233.netty.demo.chat.handler;

import com.beyond233.netty.demo.chat.session.SessionFactory;
import com.beyond233.netty.protocol.message.ChatRequestMessage;
import com.beyond233.netty.protocol.message.ChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * description: 继承SimpleChannelInboundHandler，只对指定的单一种类消息进行处理，即只处理聊天信息
 *
 * @author beyond233
 * @since 2021/5/22 17:34
 */
@ChannelHandler.Sharable
public class ChatRequestInboundHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        // 获取消息接收方的channel
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        // 接收方在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        // 接收方不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或者当前并不在线"));
        }

    }
}
