package com.beyond233.netty.demo.rpc;

import com.beyond233.netty.protocol.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 处理rpc响应消息
 *
 * @author beyond233
 * @since 2021/5/23 23:40
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    /**
     * 存放各个RPC响应消息的promise (promise用来接收另一个线程的执行结果,其作用也就是在不同线程间去传递数据)
     **/
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("服务器响应:{}", msg);

        // 拿到空的promise并将其从map中移除，节约空间，然后向promise里面放置结果
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if (promise != null) {
            if (promise.isSuccess()) {
                Object returnValue = msg.getReturnValue();
                Exception exceptionValue = msg.getExceptionValue();
                // exceptionValue不为null表示发生了异常
                if (exceptionValue != null) {
                    promise.setFailure(exceptionValue);
                } else {
                    promise.setSuccess(returnValue);
                }
            }
        }
    }
}
