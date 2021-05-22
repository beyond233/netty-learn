package com.beyond233.netty.demo.chat.handler;

import com.beyond233.netty.demo.chat.session.GroupSessionFactory;
import com.beyond233.netty.protocol.message.GroupChatRequestMessage;
import com.beyond233.netty.protocol.message.GroupChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * description: 处理群聊消息
 *
 * @author beyond233
 * @since 2021/5/22 19:43
 */
public class GroupChatRequestInboundHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();
        String groupName = msg.getGroupName();
        // 获取群里所有成员的channel并将其发送群聊消息
        List<Channel> channelList = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        for (Channel channel : channelList) {
            channel.writeAndFlush(new GroupChatResponseMessage(from, msg.getContent()));
        }
    }
}
