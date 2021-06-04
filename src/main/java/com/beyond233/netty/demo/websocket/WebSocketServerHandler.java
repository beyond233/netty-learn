package com.beyond233.netty.demo.websocket;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

/**
 * description: webSocket客户端消息处理器
 *
 * @author beyond233
 * @since 2021/6/4 23:32
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果是传统的http消息接入
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }

        // 如果是WebSocket接入
        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    /**
     * 处理Http请求消息，WebSocket的第一次握手消息由HTTP协议承载，消息头中包含Upgrade字段且其值为websocket
     *
     * @param ctx     ChannelHandlerContext
     * @param request Http请求
     * @since 2021/6/5 0:09
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 如果Http解码失败，或者请求不是建立websocket的请求，则返回HTTP异常
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory factory =
                new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            // 若handshaker为null, 则向客户端发送版本不支持的响应
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            // 若handshaker不为null, 则进行握手操作，服务端和客户端建立WebSocket连接
            handshaker.handshake(ctx.channel(), request);
        }
    }

    /**
     * 处理WebSocket消息指令
     *
     * @param ctx   ChannelHandlerContext
     * @param frame WebSocket消息帧
     * @since 2021/6/5 0:09
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }

        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 本例程只支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported !", frame.getClass().getName()));
        }

        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        log.info(String.format("%s received %s", ctx.channel(), request));
        ctx.channel().write(new TextWebSocketFrame(request + " , welcome to Netty WebSocket Service! 嘻嘻嘻"));
    }


    /**
     * 发送Http响应消息
     *
     * @param ctx      ChannelHandlerContext
     * @param request  HTTP请求
     * @param response HTTP响应
     * @since 2021/6/5 0:49
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
        // 返回应答给客户端
        if (response.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), Charsets.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
            setContentLength(response, response.content().readableBytes());
        }

        // 如果是非Keep-Alive, 则关闭与客户端的WebSocket连接
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (!isKeepAlive(request) || response.status().code() != HttpResponseStatus.OK.code()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
