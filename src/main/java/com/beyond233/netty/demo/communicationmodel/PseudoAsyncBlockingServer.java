package com.beyond233.netty.demo.communicationmodel;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * description: 伪异步IO创建的服务器，在同步阻塞服务器的基础上加了线程池来处理客户端的连接
 *
 * @author beyond233
 * @since 2021/5/30 11:03
 */
@Slf4j
public class PseudoAsyncBlockingServer {
    public static void main(String[] args) throws IOException {
        // 1. 设置服务端的端口
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2.构造ServerSocket并启动接收客户端的连接
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            log.info("服务器启动，监听端口: " + port);
            Socket socket;
            // 构建线程池，用来处理客户端连接
            ClientSocketHandlerExecutePool executePool = new ClientSocketHandlerExecutePool(20, 100);

            while (true) {
                // accept()监听客户端的连接，若无客户端接入，则主线程陷入阻塞
                socket = serverSocket.accept();
                // 3. 每有一个客户端连接上服务端时，从线程池中获取一个线程去处理这个客户端
                executePool.execute(new ClientSocketHandler(socket));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                log.info("服务器关闭！");
            }
        }
    }
}
