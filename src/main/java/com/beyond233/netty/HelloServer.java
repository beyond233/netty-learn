package com.beyond233.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * description: hello world netty
 *
 * @author beyond233
 * @since 2021/5/6 21:40
 */
public class HelloServer {
    public static void main(String[] args) {
        // 1.启动器： 负责组装netty组件，启动服务器
        new ServerBootstrap()
                // 2. BossEventLoop ,WorkerEventLoop(selector,thread), group组
                .group(new NioEventLoopGroup())
                // 3. 选择服务器的ServerSocketChannel的具体实现类
                .channel(NioServerSocketChannel.class)
                // 4. boss负责处理连接，worker（child）负责处理读写，决定了worker能执行那些操作（handler）
                .childHandler(
                        // 5. channel代表和客户端进行数据读写的通道 Initializer初始化，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 6. 添加具体handler
                        // 将 ByteBuf 转换为 String
                        nsc.pipeline().addLast(new StringDecoder());
                        // 自定义handler，重写里面的各种方法对各种事件进行自定义的处理
                        nsc.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            /**
                             * 处理读事件
                             */
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 打印上一步转换好的字符串
                                System.out.println(msg);
                            }
                        });

                    }
                })
                // 7. 绑定监听端口
                .bind(8080);
    }
}
