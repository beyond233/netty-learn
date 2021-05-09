package com.beyond233.netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * description: NettyPromise
 *
 * @author beyond233
 * @since 2021/5/9 18:07
 */
@Slf4j
public class NettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        // 自己手动创建promise,而jdk和netty的Future都是被动接收的
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(() -> { 
            log.debug("start compute");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

            // 可以自己设置返回结果
            promise.setSuccess(100);

        }).start();

        log.debug("等待结果");
        log.debug("结果是：{}", promise.get());

    }
}
