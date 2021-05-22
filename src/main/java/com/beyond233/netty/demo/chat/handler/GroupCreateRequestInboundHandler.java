package com.beyond233.netty.demo.chat.handler;

import com.beyond233.netty.demo.chat.session.Group;
import com.beyond233.netty.demo.chat.session.GroupSession;
import com.beyond233.netty.demo.chat.session.GroupSessionFactory;
import com.beyond233.netty.protocol.message.GroupCreateRequestMessage;
import com.beyond233.netty.protocol.message.GroupCreateResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * description: 处理创建群消息
 *
 * @author beyond233
 * @since 2021/5/22 17:59
 */
@ChannelHandler.Sharable
public class GroupCreateRequestInboundHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group != null) {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "该群已经存在！"));
            return;
        }
        List<Channel> channelList = groupSession.getMembersChannel(groupName);
        for (Channel channel : channelList) {
            channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群[" + groupName + "]"));
        }
        ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));


    }
}
