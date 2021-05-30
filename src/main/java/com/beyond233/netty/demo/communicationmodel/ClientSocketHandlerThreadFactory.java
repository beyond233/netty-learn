package com.beyond233.netty.demo.communicationmodel;

import com.beyond233.netty.protocol.message.SequenceIdGenerator;

import java.util.concurrent.ThreadFactory;

/**
 * description: 构造处理客户端连接的线程的工厂
 *
 * @author beyond233
 * @since 2021/5/30 11:14
 */
public class ClientSocketHandlerThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("处理客户端连接的线程-" + SequenceIdGenerator.nextId());
        return thread;
    }
}
