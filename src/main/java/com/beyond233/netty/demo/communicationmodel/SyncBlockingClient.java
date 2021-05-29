package com.beyond233.netty.demo.communicationmodel;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * description: 基于传统BIO的同步阻塞式的服务端通信模型
 *
 * @author beyond233
 * @since 2021/5/30 1:39
 */
@Slf4j
public class SyncBlockingClient {

    public static void main(String[] args) {
        // 1. 设置要连接的服务端的端口
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2.模拟有10个客户端连接服务端
        for (int i = 0; i < 10; i++) {
            new Client(port).start();
        }
    }


    /**
     * 客户端
     **/
    static class Client {
        private int port;

        public Client(int port) {
            this.port = port;
        }

        /**
         * 启动客户端
         **/
        public void start() {
            Socket socket = null;
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                socket = new Socket("localhost", port);
                log.info("客户端启动，连接服务器端口: " + port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String order = "query time order";
                out.println(order);
                log.info("成功向服务端发送命令：" + order);
                String response = in.readLine();
                log.info("服务端响应为: " + response);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        in.close();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
