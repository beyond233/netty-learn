package com.beyond233.netty.demo.rpc;

import com.beyond233.netty.demo.chat.service.HelloService;
import com.beyond233.netty.demo.chat.service.ServicesFactory;
import com.beyond233.netty.protocol.message.RpcRequestMessage;
import com.beyond233.netty.protocol.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * description: rpc请求消息处理器
 *
 * @author beyond233
 * @since 2021/5/23 23:14
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(msg.getSequenceId());

        try {
            // 1.按照请求去执行对应service的方法
            HelloService helloService = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            Method method = helloService.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object result = method.invoke(helloService, msg.getParameterValue());
            // 2.封装响应消息
            response.setReturnValue(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("RPC异常,原因: " + e.getCause().getMessage()));
        }

        // 3.响应客户端
        ctx.writeAndFlush(response);
    }
}
