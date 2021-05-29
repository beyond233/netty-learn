package com.beyond233.netty.demo.rpc;

import com.beyond233.netty.demo.chat.service.HelloService;
import com.beyond233.netty.protocol.MessageCodecSharable;
import com.beyond233.netty.protocol.ProtocolFrameDecoder;
import com.beyond233.netty.protocol.message.RpcRequestMessage;
import com.beyond233.netty.protocol.message.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * description: rpc客户端管理器
 *
 * @author beyond233
 * @since 2021/5/27 23:05
 */
@Slf4j
public class RpcClientManager {

    private static Channel channel = null;
    /**
     * sync lock
     **/
    private static final Object LOCK = new Object();


    /**
     * 获取对应class的代理类
     *
     * @param serviceClass 被代理的类对象
     * @return {@link T}
     * @since 2021/5/29 18:39
     */
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        // 方法代理执行： method-要执行的方法； args-要调用的方法的参数
        Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为RPC消息对象
            Integer sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            // 2.　将消息对象发送出去
            getChannel(8080).writeAndFlush(message);

            // 3.准备一个空的Promise对象，用来接收服务器的响应结果 (getChannel(8080).eventLoop()用来指定接收结果的线程)
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel(8080).eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId, promise);

            // 4. 等待promise结果（没拿到结果的话，线程会一直阻塞在此处）
            promise.await();
            if (promise.isSuccess()) {
                // RPC调用成功
                return promise.getNow();
            } else {
                // RPC调用异常
                log.error("发送RPC消息异常,promise接收结果失败！");
                throw new RuntimeException(promise.cause());
            }

        });

        return (T) proxyInstance;

    }

    /**
     * DCL实现singleton
     **/
    public static Channel getChannel(int port) {
        if (channel != null) { // t2
            return channel;
        }
        synchronized (LOCK) { // t1 lock
            if (channel != null) {
                return channel;
            }
            initChannel(port);
            return channel;
        }
    }

    /**
     * 初始化channel (只执行一次，保证channel是单例)
     **/
    private static void initChannel(int port) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

        // 构造bootstrap
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(loggingHandler);
                        ch.pipeline().addLast(messageCodec);
                        ch.pipeline().addLast(rpcResponseMessageHandler);
                    }
                });

        try {
            // 同步等待，直到获取到连接成功后的channel
            channel = bootstrap.connect(new InetSocketAddress(port)).sync().channel();
            // 异步关闭future
            channel.closeFuture().addListener(future -> group.shutdownGracefully());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        getChannel(8080).writeAndFlush(new RpcRequestMessage(1, "com.beyond233.netty.demo.chat.service.HelloService",
//                "sayHello", String.class, new Class[]{String.class}, new Object[]{"beyond"}));

        // 使用代理去远程调用，屏蔽底层RPC消息的转换和发送
        HelloService helloService = getProxyService(HelloService.class);
        helloService.sayHello("beyond");

    }
}
