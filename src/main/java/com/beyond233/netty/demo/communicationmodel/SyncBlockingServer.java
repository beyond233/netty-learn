package com.beyond233.netty.demo.communicationmodel;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * description: 基于传统BIO的同步阻塞式的服务端通信模型: 每有一个客户端连接就启动一个新线程去处理连接
 *
 * @author beyond233
 * @since 2021/5/30 1:09
 */
@Slf4j
public class SyncBlockingServer {

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
            Socket socket = null;
            while (true) {
                // accept()监听客户端的连接，若无客户端接入，则主线程陷入阻塞
                socket = serverSocket.accept();
                // 3. 每有一个客户端连接上服务端时，都启动一个新的线程去处理这个客户端
                new Thread(new ClientSocketHandler(socket)).start();
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

    /**
     * 负责客户端连接上服务端后的处理 : 读取客户端发来的数据并给出响应
     **/
    static class ClientSocketHandler implements Runnable {

        private Socket socket;

        public ClientSocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                // 1.获取这个客户端socket的输入输出流
                in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                out = new PrintWriter(this.socket.getOutputStream());
                String currentTime = null;
                String body = null;

                while (true) {
                    // 2.读取客户端发来的数据
                    body = in.readLine();
                    if (body == null) {
                        break;
                    }
                    log.info("服务器接收到消息: " + body);
                    // 3.向客户端响应数据
                    currentTime = "query time order".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "bad order";
                    out.println(currentTime);
                    out.flush();
                    log.info("向客户端发送消息: " + currentTime);
                }

            } catch (IOException e) {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (out != null) {
                    out.close();
                }

                try {
                    if (this.socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.socket = null;
            }
        }
    }


}
