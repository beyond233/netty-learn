package com.beyond233.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * description: hello world netty
 *
 * @author beyond233
 * @since 2021/5/6 21:40
 */
@Slf4j(topic = "HelloServer")
public class HelloServer {
    public static void main(String[] args) {
        final DefaultEventLoopGroup defaultEvg = new DefaultEventLoopGroup();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        // 1.启动器： 负责组装netty组件，启动服务器
        new ServerBootstrap()
                // 2. BossEventLoop ,WorkerEventLoop(selector,thread), group组
                .group(bossGroup,workerGroup)
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
                                log.info(msg.toString());
                                // 将消息传递给下一个handler去处理
                                ctx.fireChannelRead(msg);
                            }
                        });
                        nsc.pipeline().addLast(defaultEvg, "long-time_handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("使用默认的eventLoopGroup来负责处理耗时较长的任务，耗时短的交给nioEventLoopGroup来处理");
                            }
                        });

                    }
                })
                // 7. 绑定监听端口
                .bind(8080);
    }
}
